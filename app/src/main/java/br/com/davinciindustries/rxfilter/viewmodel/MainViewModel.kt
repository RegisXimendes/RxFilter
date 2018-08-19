package br.com.davinciindustries.rxfilter.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import br.com.davinciindustries.rxfilter.repository.MainRepository
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.rxkotlin.toObservable

class MainViewModel : ViewModel() {

    private var repository: MainRepository = MainRepository()
    val dataList = MutableLiveData<ArrayList<String>>()
    private val temporaryList = arrayListOf<String>()

    fun filter(query: String) {
        dataList.value?.clear()
        temporaryList.clear()
        repository.getMovies().toObservable()
                .filter { it.startsWith(query, true) }
                .map { movie -> movie.toUpperCase() }
                .subscribeBy(
                        onNext = {
                            temporaryList.add(it)
                        },
                        onError = { it.printStackTrace() },
                        onComplete = { dataList.value = temporaryList }
                )
    }
}