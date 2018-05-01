package common

import android.Manifest
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.annotation.VisibleForTesting
import android.support.design.widget.NavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.ContextCompat.startActivity
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import app.heroeswear.com.heroesfb.FirebaseManager
import app.heroeswear.com.heroesmakers.R
import app.heroeswear.com.heroesmakers.login.Activities.HomePageActivity
import app.heroeswear.com.heroesmakers.login.models.User
import common.controls.SmoothActionBarDrawerToggle
/**
 * Created by livnatavikasis on 29/04/2018.
 */



open class BaseActivity : AppCompatActivity() ,  NavigationView.OnNavigationItemSelectedListener {


    @VisibleForTesting
    var mProgressDialog: ProgressDialog? = null
    protected var fbManager: FirebaseManager? = null
    private var mDrawerLayout: DrawerLayout? = null
    private var mNavigationView: NavigationView? = null
    private var mDrawerToggle: SmoothActionBarDrawerToggle? = null

    private val REQUEST_ENABLE_BT = 1
    private val REQUEST_PERMISSION_ACCESS_COARSE_LOCATION = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fbManager = FirebaseManager.newInstance()
    }


    protected fun populateNavigationHeaderView(isInit: Boolean) {
        val headerView = mNavigationView?.getHeaderView(0)
        (headerView?.findViewById(R.id.lbl_name) as TextView).setText(fbManager?.mCurrentUser?.email)
        initEmpaE4()
    }

    protected fun initNavigationDrawer() {
        mDrawerLayout = findViewById(R.id.drawer_layout) as DrawerLayout
        mDrawerToggle = SmoothActionBarDrawerToggle(this, mDrawerLayout, R.string.app_name,
                R.string.app_name)
        mDrawerLayout?.setDrawerListener(mDrawerToggle)

        mNavigationView = findViewById(R.id.navigation_view) as NavigationView
//        populateNavigationHeaderView(true)
        mNavigationView?.setNavigationItemSelectedListener(this)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
//            android.R.id.home -> if (mDrawerToggle != null && !isDrawerLocked() && mDrawerToggle!!
//                            .onOptionsItemSelected(item)) {
//                        mNavigationView?.getMenu()?.findItem(DrawerItem.RIDES))
//
//            }
//            R.id.menu_call_cc -> {
//                contactCustomerCare()
//                return true
//            }
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            mDrawerToggle?.runWhenIdle(Runnable { onNavigationDrawerItemSelected(item.getItemId()) })
        } else {
            onNavigationDrawerItemSelected(item.getItemId())
        }
        mDrawerLayout?.closeDrawer(Gravity.LEFT)
        return false
    }

    protected fun isDrawerLocked(): Boolean {
        return mDrawerLayout?.getDrawerLockMode(Gravity.LEFT) == DrawerLayout.LOCK_MODE_LOCKED_CLOSED
    }

    protected fun isDrawerOpened(): Boolean? {
        return mDrawerLayout?.isDrawerOpen(Gravity.LEFT)
    }

    protected fun closeDrawer() {
        mDrawerLayout?.closeDrawer(Gravity.LEFT)
    }

    fun onNavigationDrawerItemSelected(id: Int) {

        when (id) {
            DrawerItem.SETTINGS -> {
//                    openHome(MixpanelUtils.SCREEN_DRAWER)
            }
        }
    }

    fun showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog(this)
            mProgressDialog!!.setMessage(getString(R.string.loading))
            mProgressDialog!!.isIndeterminate = true
        }

        mProgressDialog!!.show()
    }

    fun hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog!!.isShowing) {
            mProgressDialog!!.dismiss()
        }
    }

//    fun hideKeyboard(view: View) {
//        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//        imm?.hideSoftInputFromWindow(view.windowToken, 0)
//    }

    override fun onStop() {
        super.onStop()
        hideProgressDialog()
    }

    fun openHomePage(user: User) {
        val intent = Intent(this, HomePageActivity::class.java)
        intent.putExtra("user", user)
        startActivity(intent)
    }


     fun initEmpaE4() {
        if (ContextCompat.checkSelfPermission
                (this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), REQUEST_PERMISSION_ACCESS_COARSE_LOCATION)
        } else {
            val intent = Intent(this, EmpaE4::class.java)
            startService(intent)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_PERMISSION_ACCESS_COARSE_LOCATION ->
                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission was granted, yay!
                    val intent = Intent(this, EmpaE4::class.java)
                    startService(intent)
                } else {
                    // Permission denied, boo!
                    val needRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    AlertDialog.Builder(this)
                            .setTitle("Permission required")
                            .setMessage("Without this permission bluetooth low energy devices cannot be found, allow it in order to connect to the device.")
                            .setPositiveButton("Retry") { dialog, which ->
                                // try again
                                if (needRationale) {
                                    // the "never ask again" flash is not set, try again with permission request
                                    val intent = Intent(this, EmpaE4::class.java)
                                    startService(intent)
                                } else {
                                    // the "never ask again" flag is set so the permission requests is disabled, try open app settings to enable the permission
                                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                    val uri = Uri.fromParts("package", packageName, null)
                                    intent.data = uri
                                    startActivity(intent)
                                }
                            }
                            .setNegativeButton("Exit application") { dialog, which ->
                                // without permission exit is the only way
                                finish()
                            }
                            .show()
                }
        }
    }
}


