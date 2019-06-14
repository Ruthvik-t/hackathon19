

package com.piedpipers.sawy

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.PlaybackPreparer
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerControlView
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.activity_recipe.*
import org.json.JSONObject
import java.io.File
import java.nio.charset.Charset


/**
 * Loads [RecipeFragment].
 */
class RecipeActivity : Activity(), View.OnClickListener, PlaybackPreparer, PlayerControlView.VisibilityListener {

    private lateinit var playerView: PlayerView

    override fun onClick(p0: View?) {
    }

    override fun preparePlayback() {
    }

    override fun onVisibilityChange(visibility: Int) {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe)
        initVideoPlayer()
    }

    private fun initVideoPlayer() {
        val path = Environment.getExternalStorageDirectory().path + "/Download/video.mp4"
        val file = File(path)
        val player = ExoPlayerFactory.newSimpleInstance(this) as SimpleExoPlayer
        player.setPlayWhenReady(true)
        playerView = video_view
        playerView.setPlayer(player)
        playerView.setPlaybackPreparer(this)
        playerView.setControllerVisibilityListener(this)
        val dataSourceFactory = DefaultDataSourceFactory(
            this,
            Util.getUserAgent(this, "SAYW")
        )
        val videoSource = ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(Uri.fromFile(file))
        player.prepare(videoSource)
        val recipeDetails = getJson()
        product_carousel.adapter = ProductCarouselAdapter(this, recipeDetails.items, LayoutInflater.from(this))
        val handler = Handler()

        val r = object : Runnable {
            override fun run() {
                val currPosition = player.currentPosition / 1000
                for (i in 0 until recipeDetails.items.size - 1) {
                    val item1 = recipeDetails.items[i]
                    val item2 = recipeDetails.items[i + 1]
                    if (currPosition > item1.time && currPosition < item2.time) {
                        product_view_flipper.displayedChild = 1
                        product_carousel.setCurrentItem(i, true)
                        break
                    } else if (currPosition > recipeDetails.items.last().time) {
                        product_view_flipper.displayedChild = 1
                        product_carousel.setCurrentItem(recipeDetails.items.lastIndex, true)
                        break
                    } else if (currPosition < recipeDetails.items.first().time) {
                        product_view_flipper.displayedChild = 0
                        break
                    }
                }
                handler.postDelayed(this, 100)
            }
        }

        handler.postDelayed(r, 0)
    }

    private fun getJson(): RecipeJson {
        val json = this.assets.open("recipe.json")
        val size = json.available()
        val buffer = ByteArray(size)
        json.read(buffer)
        json.close()
        val myJson = String(buffer, Charset.forName("UTF-8"))
        val jsonObject = JSONObject(myJson)
        val recipeDetails = RecipeJson(jsonObject.getString("name"), jsonObject.getString("code"), getItems(jsonObject))
        return recipeDetails
    }

    private fun getItems(jsonObject: JSONObject): List<Item> {
        var list = mutableListOf<Item>()
        val array = jsonObject.getJSONArray("items")
        for (i in 0 until array.length()) {
            val o = array.getJSONObject(i)
            list.add(
                Item(
                    name = o.getString("name"),
                    imageName = o.getString("imageName"),
                    time = o.getDouble("time"),
                    price = o.getDouble("price")
                )
            )
        }
        return list
    }

}
