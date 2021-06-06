package ru.totowka.myapplication.ui

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import ru.totowka.myapplication.R
import ru.totowka.myapplication.model.GifState

class MainActivity : AppCompatActivity(), View.OnClickListener {
    companion object {
        private const val BASE_URL = "https://developerslife.ru/random?json=true"
    }

    lateinit var gif : ImageView
    lateinit var queue : RequestQueue
    lateinit var backButton : Button
    lateinit var updateButton : Button
    lateinit var title : TextView
    lateinit var showcase_pb : ProgressBar
    lateinit var showcase_view : View

    var currentGif : GifState? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        queue = Volley.newRequestQueue(this)
        gif = findViewById(R.id.gif)
        backButton = findViewById(R.id.back_button)
        updateButton = findViewById(R.id.next_button)
        title = findViewById(R.id.title)
        showcase_pb = findViewById(R.id.showcase_pb)
        showcase_view = findViewById(R.id.showcase_view)
        backButton.setOnClickListener(this)
        backButton.isClickable = false
        updateButton.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.back_button -> {
                showGif(currentGif?.lastState!!)
            }
            R.id.next_button -> {
                window.setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                )
                showcase_pb.visibility = View.VISIBLE
                showcase_view.visibility = View.VISIBLE
                val jsonObjectRequest = JsonObjectRequest(
                    Request.Method.GET, BASE_URL, null,
                    { response ->
                        val gifUrl = response.getString("gifURL")
                        val gifTitle = response.getString("description")
                        val state = GifState(gifTitle, gifUrl, currentGif)
                        showGif(state)
                    },
                    { error ->
                        Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
                    }
                )
                queue.add(jsonObjectRequest)
                showcase_pb.visibility = View.INVISIBLE
                showcase_view.visibility = View.INVISIBLE
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        }
    }

    fun showGif(state: GifState) {
        currentGif = state
        Glide.with(this)
            .load(state.url)
            .placeholder(R.drawable.ic_embroidery_colored)
            .error(R.drawable.ic_broken_image)
            .fallback(R.drawable.ic_gif)
            .into(gif)
        title.text = state.title
        backButton.isClickable = state.lastState != null

//            .centerCrop()

    }
}