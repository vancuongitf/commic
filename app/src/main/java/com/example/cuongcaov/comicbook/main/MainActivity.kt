package com.example.cuongcaov.comicbook.main

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.LinearLayoutManager
import android.view.Gravity
import android.view.Menu
import android.view.View
import android.widget.Toast
import com.example.cuongcaov.comicbook.R
import com.example.cuongcaov.comicbook.base.BaseActivity
import com.example.cuongcaov.comicbook.networking.APIResult
import com.example.cuongcaov.comicbook.networking.RetrofitClient
import com.example.cuongcaov.comicbook.storydetail.DetailFragment
import com.example.cuongcaov.comicbook.storydetail.StoryDetailActivity
import com.miguelcatalan.materialsearchview.MaterialSearchView
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.NetworkInterface
import java.util.*


/**
 * MainActivity.
 *
 * @author CuongCV
 */
class MainActivity : BaseActivity() {

    companion object {

        const val ACTION_LIKE = "action_like"

        fun getMacAddr(): String {
            try {
                val all = Collections.list(NetworkInterface.getNetworkInterfaces())
                for (nif in all) {
                    if (!(nif.getName().toLowerCase() == "wlan0")) continue

                    val macBytes = nif.getHardwareAddress() ?: return ""

                    val res1 = StringBuilder()
                    for (b in macBytes) {
                        res1.append(String.format("%02X:", b))
                    }

                    if (res1.length > 0) {
                        res1.deleteCharAt(res1.length - 1)
                    }
                    return res1.toString()
                }
            } catch (ex: Exception) {
            }
            return ""
        }

    }

    private val mComics = mutableListOf<Comic>()
    private var mCurrentPage = 1
    private var mPageCount = 1
    private var mQuery = ""
    private var mGetByType = false
    private var mTypeToGet: Int = 0

    private var mAdapter = StoryListAdapter(mComics, getMacAddr(), object : MenuAdapter.RecyclerViewOnItemClickListener {
        override fun onLikeAction(storyId: Long, liked: Boolean) {
            savedStory(KEY_LIKE, storyId, liked)
        }

        override fun onItemClick(item: Any) {
            val comic = item as? Comic
            if (comic != null) {
                val intent = Intent(this@MainActivity, StoryDetailActivity::class.java)
                val bundle = Bundle()
                savedStory(KEY_HISTORY, comic.storyId)
                RetrofitClient.getAPIService().read(comic.storyId)
                        .enqueue(object : Callback<Int> {
                            override fun onFailure(call: Call<Int>?, t: Throwable?) = Unit

                            override fun onResponse(call: Call<Int>?, response: Response<Int>?) {
                                if (response?.body() != null) {
                                    comic.readCount = response.body()!!
                                }
                            }

                        })
                comic.seen = true
                comic.readCount++
                bundle.putSerializable(DetailFragment.KEY_COMIC, comic)
                intent.putExtras(bundle)
                startActivity(intent)
            }
        }

    })
    private val mMenuAdapter = MenuAdapter(object : MenuAdapter.RecyclerViewOnItemClickListener {

        override fun onItemClick(item: Any) {
            val menuItem = item as? MenuItem
            if (menuItem != null) {
                tvTitle.text = menuItem.typeName
                mGetByType = true
                mTypeToGet = menuItem.typeId
                mCurrentPage = 1
                getStory()
            }
            drawerLayout.closeDrawers()
            searchView.closeSearch()
        }
    })

    private val mReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val comic = intent.extras.getSerializable(ACTION_LIKE) as? Comic
            if (comic != null) {
                val comic1 = mComics.find {
                    it.storyId == comic.storyId
                }
                comic1?.like = comic.like
                comic1?.likeCount = comic.likeCount
                savedStory(KEY_LIKE, comic.storyId, comic.like)
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        initDrawer()
        onClick()
        registerReceiver(mReceiver, IntentFilter(ACTION_LIKE))
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        searchView.setMenuItem(menu?.findItem(R.id.mnuSearch))
        return true
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        when (item.itemId) {
            R.id.mnuLike -> {
            }
            R.id.mnuRead -> {
            }
        }
        return false
    }

    override fun onResume() {
        super.onResume()
        mAdapter.notifyDataSetChanged()
    }

    private fun initView() {
        setSupportActionBar(toolbar)
        title = ""
        recyclerViewComics.layoutManager = LinearLayoutManager(this@MainActivity)
        recyclerViewComics.adapter = mAdapter
        imgOpenDrawer.setOnClickListener {
            drawerLayout.openDrawer(Gravity.START)
        }
        getStories()
        swipeRefreshLayout.setOnRefreshListener {
            getStory()
        }
        searchView.setOnQueryTextListener(object : MaterialSearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                mCurrentPage = 1
                mQuery = if (query == null) {
                    ""
                } else {
                    "WHERE STORY_NAME LIKE '%$query%' "
                }
                mGetByType = false
                mTypeToGet = 0
                mCurrentPage = 1
                getStory()
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean = false

        })
    }

    private fun initDrawer() {
        recyclerViewTypeMenu.layoutManager = LinearLayoutManager(this)
        recyclerViewTypeMenu.adapter = mMenuAdapter
        val drawerToggle = object : ActionBarDrawerToggle(this, drawerLayout, R.string.app_name, R.string.app_name) {
            override fun onDrawerClosed(drawerView: View?) {
                super.onDrawerClosed(drawerView)
                imgOpenDrawer.setImageResource(R.drawable.ic_menu_white_48dp)
            }

            override fun onDrawerOpened(drawerView: View?) {
                super.onDrawerOpened(drawerView)
                imgOpenDrawer.setImageResource(R.drawable.ic_arrow_back_white_48dp)
            }
        }
        drawerLayout.addDrawerListener(drawerToggle)
        drawerLayout.setScrimColor(Color.TRANSPARENT)
        drawerToggle.syncState()
    }

    private fun onClick() {

        tvTypeAll.setOnClickListener {
            if (mGetByType) {
                mGetByType = false
                mTypeToGet = 0
                mCurrentPage = 1

                getStory()
                drawerLayout.closeDrawers()
                mMenuAdapter.deleteSelected()
            }
        }
        imgToFirstPage.setOnClickListener {
            if (mCurrentPage > 1) {
                mCurrentPage = 1
            }
            getStory()
        }
        imgPreviousPage.setOnClickListener {
            if (mCurrentPage > 1) {
                mCurrentPage--
            }
            getStory()
        }
        imgNextPage.setOnClickListener {
            if (mCurrentPage < mPageCount) {
                mCurrentPage++
            }
            getStory()
        }
        imgToLastPage.setOnClickListener {
            if (mCurrentPage < mPageCount) {
                mCurrentPage = mPageCount
            }
            getStory()
        }
    }

    private fun getStory() {
        if (mGetByType) {
            if (mTypeToGet != 0) {
                getStoriesByType()
            }
        } else {
            getStories()
        }
    }

    private fun getStories() {
        val thread = Thread({
            val apiService = RetrofitClient.getAPIService()
            apiService.getStories(mCurrentPage, mQuery).enqueue(object : Callback<APIResult> {
                override fun onResponse(call: Call<APIResult>?, response: Response<APIResult>?) {
                    val list = response?.body()?.data
                    val storyCount = response?.body()?.count
                    updateFooter(storyCount)
                    getSavedStory(KEY_LIKE)
                    getSavedStory(KEY_HISTORY)
                    list?.forEach {
                        if (mLiked.contains(it.storyId.toString())) {
                            it.like = true
                        }
                        if (mHistory.contains(it.storyId.toString())) {
                            it.seen = true
                        }
                    }
                    if (list != null) {
                        runOnUiThread {
                            mComics.clear()
                            mComics.addAll(list)
                            mAdapter.notifyDataSetChanged()
                            swipeRefreshLayout.isRefreshing = false
                        }
                    }
                }

                override fun onFailure(call: Call<APIResult>?, t: Throwable?) {
                    runOnUiThread {
                        Toast.makeText(this@MainActivity, "Xãy ra lỗi.", Toast.LENGTH_LONG).show()
                        swipeRefreshLayout.isRefreshing = false
                    }
                }

            })
        })
        thread.start()
        swipeRefreshLayout.isRefreshing = true
    }

    private fun getStoriesByType() {
        mComics.clear()
        mAdapter.notifyDataSetChanged()
        mCurrentPage = 1
        updateFooter(1)
        val thread = Thread({
            val apiService = RetrofitClient.getAPIService()
            apiService.getStoriesByType(mCurrentPage, mTypeToGet).enqueue(object : Callback<APIResult> {
                override fun onResponse(call: Call<APIResult>?, response: Response<APIResult>?) {
                    val list = response?.body()?.data
                    val storyCount = response?.body()?.count
                    updateFooter(storyCount)
                    getSavedStory(KEY_LIKE)
                    getSavedStory(KEY_HISTORY)
                    list?.forEach {
                        if (mLiked.contains(it.storyId.toString())) {
                            it.like = true
                        }
                        if (mHistory.contains(it.storyId.toString())) {
                            it.seen = true
                        }
                    }
                    if (list != null) {
                        runOnUiThread {
                            mComics.clear()
                            mComics.addAll(list)
                            mAdapter.notifyDataSetChanged()
                            swipeRefreshLayout.isRefreshing = false
                        }
                    }
                }

                override fun onFailure(call: Call<APIResult>?, t: Throwable?) {
                    Toast.makeText(this@MainActivity, "Danh sách trống.", Toast.LENGTH_LONG).show()
                    swipeRefreshLayout.isRefreshing = false
                }

            })
        })
        thread.start()
        swipeRefreshLayout.isRefreshing = true
    }

    private fun updateFooter(storyCount: Int?) {
        mPageCount = if (storyCount == null) {
            1
        } else {
            if (storyCount % 20 > 0) {
                storyCount / 20 + 1
            } else {
                storyCount / 20
            }

        }
        if (mCurrentPage < 2) {
            imgPreviousPage.isEnabled = false
            imgToFirstPage.isEnabled = false
        } else {
            imgPreviousPage.isEnabled = true
            imgToFirstPage.isEnabled = true
        }
        if (mCurrentPage > mPageCount - 1) {
            imgNextPage.isEnabled = false
            imgToLastPage.isEnabled = false
        } else {
            imgNextPage.isEnabled = true
            imgToLastPage.isEnabled = true
        }
        tvCurrentPage.text = getString(R.string.footerText, mCurrentPage, mPageCount)
    }


}
