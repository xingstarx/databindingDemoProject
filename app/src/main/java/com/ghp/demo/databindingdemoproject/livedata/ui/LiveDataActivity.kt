package com.ghp.demo.databindingdemoproject.livedata.ui

import android.arch.lifecycle.*
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.ghp.demo.databindingdemoproject.R
import com.ghp.demo.databindingdemoproject.databinding.ActivityLiveDataBinding
import com.ghp.demo.databindingdemoproject.extension.addClickAction
import com.ghp.demo.databindingdemoproject.livedata.data.TestLiveData
import com.ghp.demo.databindingdemoproject.livedata.viewmodel.NameViewModel
import com.ghp.demo.databindingdemoproject.testmodel.UserModel
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.LiveData


class LiveDataActivity : AppCompatActivity() {
    private val TAG = "LiveDataActivityTest"
    lateinit var mNameViewModel: NameViewModel
    lateinit var binding: ActivityLiveDataBinding
    var userLiveData: MutableLiveData<UserModel> = MutableLiveData()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mNameViewModel = ViewModelProviders.of(this).get(NameViewModel::class.java)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_live_data)

        mNameViewModel.mCurrentName.observe(this, Observer { name: String? ->
            binding.nameText.text = name ?: ""
        })

        mNameViewModel.mNameLiveData!!.observe(this, Observer { nameList: List<String>? ->
            nameList?.apply {
                var listStr: String = ""
                for (item in nameList) {
                    listStr = listStr + "\n" + item
                }
                binding.nameListText.text = listStr
            }
        })

        binding.nameBtn.addClickAction {
            mNameViewModel.mCurrentName?.value = "liveData"
        }

        binding.nameListBtn.addClickAction {
            val nameList: MutableList<String> = mutableListOf()
            for (i in 0..9) {
                nameList.add("Jane<$i>")
            }
            mNameViewModel.mNameLiveData?.value = nameList
        }

        TestLiveData().getInstance(this)?.observe(this, Observer { wifiLevel ->
            binding.testLivedataText.text = wifiLevel.toString()
        })

        mNameViewModel.userNameLiveData!!.observe(this, Observer { userName: String? ->
            binding.transformationsText.text = userName
        })

        /**
         * 需要在LiveData将变化的数据通知给观察者前，改变数据的类型
         * 直接给source返回为null
         */
        var userNameLiveData: LiveData<String> = Transformations.map<UserModel, String>(userLiveData, { user ->
            user.userName
        })

        userNameLiveData.observe(this, Observer { userName ->
            mNameViewModel.userNameLiveData?.value = userNameLiveData.value
        })

        binding.transformationsLivedataBtn.addClickAction {
            userLiveData.value = mNameViewModel.getUserLiveData().value
        }


    }

}