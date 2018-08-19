package br.com.davinciindustries.rxfilter.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import br.com.davinciindustries.rxfilter.repository.MainRepository
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.rxkotlin.toObservable

class MainViewModel : ViewModel() {

    private var repository: MainRepository = MainRepository()
    val moviesList = MutableLiveData<ArrayList<String>>()
    val list = arrayListOf<String>()

    fun filter(query: String) {
        moviesList.value?.clear()
        list.clear()
        repository.getMovies().toObservable()
                .filter { it.startsWith(query, true) }
                .map { movie -> movie.toUpperCase() }
                .subscribeBy(
                        onNext = {
                            list.add(it)
                        },
                        onError = { it.printStackTrace() },
                        onComplete = {  moviesList.value = list}
                )
    }
}