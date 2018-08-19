package br.com.davinciindustries.rxfilter.view

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import br.com.davinciindustries.rxfilter.R
import br.com.davinciindustries.rxfilter.databinding.ActivityMainBinding
import br.com.davinciindustries.rxfilter.viewmodel.MainViewModel
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    lateinit var viewModel: MainViewModel
    lateinit var binding: ActivityMainBinding

    private var itemsAdapter: ArrayAdapter<String>? = null

    private var compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.viewModel = viewModel
        binding.setLifecycleOwner(this)
        setContentView(binding.root)

        setupList()
        observeEditText()
        observeViewModel()
    }

    private fun setupList() {
        itemsAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1)
        binding.lvText.adapter = itemsAdapter
    }

    private fun observeEditText() {
        val obs = RxTextView.textChanges(etText)
                .debounce(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .map { char -> char.toString() }
                .subscribe { query -> viewModel.filter(query) }

        compositeDisposable.add(obs)
    }

    private fun observeViewModel() {
        viewModel.dataList.observe(this, Observer { it ->
            it?.let {
                setupAdapter(it)
            }
        })
    }

    private fun setupAdapter(textList: ArrayList<String>){
        itemsAdapter?.clear()
        itemsAdapter?.addAll(textList)
        itemsAdapter?.notifyDataSetChanged()
    }

    override fun onStop() {
        super.onStop()
        compositeDisposable.clear()
    }

}
