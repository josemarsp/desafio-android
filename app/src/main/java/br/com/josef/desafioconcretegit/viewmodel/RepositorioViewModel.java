package br.com.josef.desafioconcretegit.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import br.com.josef.desafioconcretegit.model.data.CacheDao;
import br.com.josef.desafioconcretegit.model.data.DatabaseGit;
import br.com.josef.desafioconcretegit.model.data.repository.GitRepository;
import br.com.josef.desafioconcretegit.model.pojo.pull.PullRequest;
import br.com.josef.desafioconcretegit.model.pojo.repositories.GitResult;
import br.com.josef.desafioconcretegit.model.pojo.repositories.Item;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static br.com.josef.desafioconcretegit.util.AppUtil.isNetworkConnected;

public class RepositorioViewModel extends AndroidViewModel {

    private MutableLiveData<List<Item>> listaDeRepositorios = new MutableLiveData<>();
    private GitRepository gitRepository = new GitRepository();
    private CompositeDisposable disposable = new CompositeDisposable();
    private MutableLiveData<List<PullRequest>> requestList = new MutableLiveData<>();
    private MutableLiveData<Boolean> booleano = new MutableLiveData<>();

    public RepositorioViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<List<Item>> getListaRespositorios() {
        return this.listaDeRepositorios;
    }

    public LiveData<List<PullRequest>> getRequest() {
        return this.requestList;
    }

    public LiveData<Boolean> getBooleano() {
        return this.booleano;
    }

    public void getRepositorioNetwork (int page){
        if (isNetworkConnected(getApplication())){
            getRepositorios(page);
        }else{
            getFromLocal();
        }
    }


    public void getRepositorios(int page) {
        disposable.add(
                gitRepository.getRepositorios(page)

                        .subscribeOn(Schedulers.newThread())
                        .map(this::saveItems)
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe(disposable1 -> booleano.setValue(true))
                        .doOnTerminate(() -> booleano.setValue(false))
                        .subscribe(result1 -> {
                            listaDeRepositorios.setValue(result1.getItems());
                        }, throwable -> {

                            Log.i("LOG", "Error: " + throwable.getMessage());
                        })


        );
    }

    private GitResult saveItems(GitResult gitResult) {
        CacheDao dao = DatabaseGit
                .getDatabase(getApplication()
                        .getApplicationContext())
                .cacheDao();
        dao.deleteAll();
        dao.insert(gitResult.getItems());
        return gitResult;
    }


    private void getFromLocal() {
        disposable.add(
                gitRepository.getLocalResults(getApplication().getApplicationContext())
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(results -> {
                           listaDeRepositorios.setValue(results);
                        }, throwable -> {
                            Log.i("LOG", "erro buscando OffLine " + throwable.getMessage());
                        })
        );

    }


    public void getPullRequest(String creatorString, String repoString) {
        disposable.add(
                gitRepository.getPullRequest(creatorString, repoString)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe(disposable1 -> booleano.setValue(true))
                        .doOnTerminate(() -> booleano.setValue(false))
                        .subscribe(request1 -> {
                            requestList.setValue(request1);

                        }, throwable -> {
                            Log.i("Log", "erro " + throwable.getMessage());
                        })


        );
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();
    }
}