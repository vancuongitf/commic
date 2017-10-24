package com.example.cuongcaov.comicbook.main

import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.LinearLayoutManager
import android.view.Gravity
import android.view.Menu
import android.view.View
import com.example.cuongcaov.comicbook.R
import com.example.cuongcaov.comicbook.base.BaseActivity
import com.example.cuongcaov.comicbook.model.MenuItem
import com.miguelcatalan.materialsearchview.MaterialSearchView
import kotlinx.android.synthetic.main.activity_main.*
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

    private var mQuery = ""

    private val mMenuAdapter = MenuAdapter(object : MenuAdapter.RecyclerViewOnItemClickListener {

        override fun onItemClick(item: Any) {
            val menuItem = item as? MenuItem
            if (menuItem != null) {
                replaceFragment(TypeFragment.getInstance(menuItem.typeId), menuItem.typeName)
            }
            drawerLayout.closeDrawers()
            searchView.closeSearch()
        }
    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mains)
        initView()
        initDrawer()
        onClick()
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        searchView.setMenuItem(menu?.findItem(R.id.mnuSearch))
        return true
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        when (item.itemId) {
            R.id.mnuLike -> {
                replaceFragment(SavedFragment.getInstance(true), "Truyện yêu thích")
            }
            R.id.mnuRead -> {
                replaceFragment(SavedFragment.getInstance(), "Truyện đã đọc")
            }
        }
        return false
    }

    private fun initView() {
        setSupportActionBar(toolbar)
        title = ""
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.flMainContent, MainFragment.getInstance())
        transaction.commit()
        searchView.setOnQueryTextListener(object : MaterialSearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                mQuery = if (query == null) {
                    ""
                } else {
                    "WHERE STORY_NAME LIKE '%$query%' "
                }
                val mainFragment = supportFragmentManager.findFragmentById(R.id.flMainContent) as? MainFragment
                if (mainFragment != null) {
                    mainFragment.setQuery(mQuery)
                } else {
                    replaceFragment(MainFragment.getInstance(mQuery), getString(R.string.app_name))
                }
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
            drawerLayout.closeDrawers()
            mMenuAdapter.deleteSelected()
            replaceFragment(MainFragment.getInstance(), getString(R.string.app_name))
        }

        imgOpenDrawer.setOnClickListener {
            drawerLayout.openDrawer(Gravity.START)
        }
    }

    private fun replaceFragment(fragment: Fragment, title: String) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.flMainContent, fragment)
        transaction.commit()
        tvTitle.text = title
    }
}