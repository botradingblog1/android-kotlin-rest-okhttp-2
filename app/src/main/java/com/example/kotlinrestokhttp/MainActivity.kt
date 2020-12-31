package com.example.kotlinrestokhttp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView

// JSON parse
import com.beust.klaxon.Klaxon

// Imports for
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.lifecycle.lifecycleScope

// Networking
import java.io.BufferedReader
import java.io.InputStream
import java.net.URL
import javax.net.ssl.HttpsURLConnection

// View model
import androidx.lifecycle.Observer
import androidx.activity.viewModels
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

class MainActivity : AppCompatActivity() {
    var textViewTitle: TextView? = null
    var textViewDescription: TextView? = null
    var buttonFetch: Button? = null
    val viewModel: MainActivityViewModel by viewModels()

    // Create OkHttp Client
    var client: OkHttpClient = OkHttpClient();

    private fun getRequest(sUrl: String): String? {
        var result: String? = null

        try {
            // Create URL
            val url = URL(sUrl)

            // Build request
            val request = Request.Builder().url(url).build()

            // Execute request
            val response = client.newCall(request).execute()
            result = response.body?.string()
        }
        catch(err:Error) {
            print("Error when executing get request: "+err.localizedMessage)
        }

        return result
    }

    private fun fetch(sUrl: String): BlogInfo? {
        var blogInfo: BlogInfo? = null
        lifecycleScope.launch(Dispatchers.IO) {
            val result = getRequest(sUrl)
            if (result != null) {
                try {
                    // Parse result string JSON to data class
                    blogInfo = Klaxon().parse<BlogInfo>(result)

                    withContext(Dispatchers.Main) {
                        // Update view model
                        viewModel.title.value = blogInfo?.title
                        viewModel.description.value = blogInfo?.description
                    }
                }
                catch(err:Error) {
                    print("Error when parsing JSON: "+err.localizedMessage)
                }
            }
            else {
                print("Error: Get request returned no response")
            }
        }
        return blogInfo
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        textViewTitle = findViewById(R.id.textview_blog_title)
        textViewDescription = findViewById(R.id.textview_blog_description)
        buttonFetch = findViewById(R.id.button_fetch)

        buttonFetch?.setOnClickListener(View.OnClickListener {
            // Launch get request
            fetch("https://raw.githubusercontent.com/justmobiledev/android-kotlin-rest-1/main/support/data/bloginfo.json")
        })

        viewModel.title.observe(this, Observer {
            textViewTitle?.text = it
        })

        viewModel.description.observe(this, Observer {
            textViewDescription?.text = it
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}