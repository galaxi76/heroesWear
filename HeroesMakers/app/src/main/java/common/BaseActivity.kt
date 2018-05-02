package common

import android.Manifest
import android.app.ProgressDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.support.annotation.VisibleForTesting
import android.support.design.widget.NavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import app.heroeswear.com.heroesfb.FirebaseManager
import app.heroeswear.com.heroesmakers.R
import app.heroeswear.com.heroesmakers.login.Activities.EmergencyContactsActivity
import app.heroeswear.com.heroesmakers.login.Activities.HomePageActivity
import app.heroeswear.com.heroesmakers.login.models.User
import common.controls.SmoothActionBarDrawerToggle
/**
 * Created by livnatavikasis on 29/04/2018.
 */



open class BaseActivity : AppCompatActivity() ,  NavigationView.OnNavigationItemSelectedListener {


    @VisibleForTesting
    var mProgressDialog: ProgressDialog? = null
    var fbManager: FirebaseManager? = null
    protected var mDrawerLayout: DrawerLayout? = null
    protected var mNavigationView: NavigationView? = null
    protected var mDrawerToggle: ActionBarDrawerToggle? = null
    protected var toolbar: Toolbar? = null
    private val REQUEST_ENABLE_BT = 1
    private val REQUEST_PERMISSION_ACCESS_COARSE_LOCATION = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fbManager = FirebaseManager.newInstance()
    }

    override fun onBackPressed() {
        mDrawerLayout?.let {
            if (mDrawerLayout!!.isDrawerOpen(GravityCompat.START)) {
                mDrawerLayout?.closeDrawer(GravityCompat.START)
            } else {
                super.onBackPressed()
            }
            return
        }
          super.onBackPressed()

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        return if (id == R.id.action_call) {
            phoneDial(baseContext,"911")
            return true
        } else {
            super.onOptionsItemSelected(item)
        }

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.nav_favorites) {

        } else if (id == R.id.nav_contact) {
            openEmergencyContacts()

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_settings) {

        }

        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        drawer.closeDrawer(GravityCompat.START)

        return true
    }

    fun initMenues() {
        if (toolbar == null ) {
            toolbar = findViewById(R.id.home_toolbar) as Toolbar
            setSupportActionBar(toolbar)
        }
        if (mDrawerLayout == null){
            mDrawerLayout = findViewById(R.id.drawer_layout)
        }
        if (mDrawerToggle == null){
            mDrawerToggle = ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
            mDrawerLayout?.addDrawerListener(mDrawerToggle!!)
        }
        mDrawerToggle?.syncState()

        mNavigationView = findViewById<View>(R.id.navigation_view) as NavigationView
        mNavigationView?.getHeaderView(0)?.findViewById<TextView>(R.id.lbl_name)?.text = fbManager?.mCurrentUser?.email
        mNavigationView?.setNavigationItemSelectedListener(this)
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

    fun openEmergencyContacts() {
        val intent = Intent(this, EmergencyContactsActivity::class.java)
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

    fun phoneDial(context: Context, phone: String?) {
        if (phone == null) {
            return
        }
        try {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:$phone")
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
        }

    }
}


