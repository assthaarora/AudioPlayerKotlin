package com.shopvite.audioplayerkotlin.adapter

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.shopvite.audioplayerkotlin.R
import com.shopvite.audioplayerkotlin.model.AudioModel
import com.shopvite.audioplayerkotlin.viewdmodel.AudioViewModel
import java.io.IOException


class AudioRecyclerAdapter (val viewModel: AudioViewModel, val arrayList: ArrayList<AudioModel>, val context: Context): RecyclerView.Adapter<AudioRecyclerAdapter.AudioViewHolder>() {
    private lateinit var mediaPlayer:MediaPlayer
    private val wasPlaying = false
    private var aposition = -1
    private var thread: Thread? = null
    private var flag:Boolean=false

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): AudioRecyclerAdapter.AudioViewHolder {
        var root = LayoutInflater.from(parent.context).inflate(R.layout.card_view_ccustom,parent,false)
        return AudioViewHolder(root)
    }

    override fun onBindViewHolder(holder: AudioRecyclerAdapter.AudioViewHolder, position: Int) {
        val model=arrayList.get(position)
        holder.audioName.text =model.fileName
        if (position == aposition) {
//            holder.playButton.setBackgroundResource(0)
            holder.playButton.setImageDrawable(
                ContextCompat.getDrawable(
                    context,
                    R.drawable.pause_button
                )
            )
        } else {
//            holder.playButton.setBackgroundResource(0)
            holder.playButton.setImageDrawable(
                ContextCompat.getDrawable(
                    context,
                    R.drawable.play_button
                )
            )
        }
        holder.playButton.setOnClickListener(View.OnClickListener {
            Log.i("Adapterposition444", "$position%%$aposition")
            playAudio(model.filePath, holder.adapterPosition,
                holder.audioBar, holder.playButton)

        })

    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        val manager = recyclerView.layoutManager
        if (manager is LinearLayoutManager && itemCount > 0) {
            val llm = manager
            recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                }

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val firstvisiblePosition = llm.findFirstCompletelyVisibleItemPosition()
                    val lastvisiblePosition=llm.findLastCompletelyVisibleItemPosition()
                    if (aposition>-1 && !(firstvisiblePosition <= aposition && aposition <= lastvisiblePosition)) {
                        if(flag) {
                            if (mediaPlayer.isPlaying){
                                clearMediaPlayer()
                            }

                            flag=false
                        }

                    }
                }
            })
        }
    }


    override fun getItemCount(): Int {
        if(arrayList.size==0){
            Toast.makeText(context,"List is empty",Toast.LENGTH_LONG).show()
        }
        return arrayList.size
    }


    inner  class AudioViewHolder(private val binding: View) : RecyclerView.ViewHolder(binding) {
        val playButton: ImageButton = binding.findViewById(R.id.playButton)
        val audioName: TextView = binding.findViewById(R.id.audioFileName)
        val audioBar: SeekBar=binding.findViewById(R.id.audioBar)
    }

    private fun playAudio(audioUrl: String, a_position: Int, seekBar: SeekBar, ib: ImageButton) {
        // initializing media player
        if(flag){
            if ( mediaPlayer.isPlaying()) {
                clearMediaPlayer()
                thread!!.interrupt()
                seekBar.progress = 0
                ib.setBackgroundResource(0)
                ib.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.play_button
                    )
                )
            }
            flag=false
        }


        // initializing media player
        Log.i("Adapterposition", "$a_position##$aposition")

        if (aposition != a_position) {
            // below line is use to set our
            // url to our media player.
            Log.d("ExceptioAudio", audioUrl)
            try {
                mediaPlayer = MediaPlayer()
                // below line is use to set the audio
                // stream type for our media player.
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
                mediaPlayer.setDataSource(audioUrl)
                // below line is use to prepare
                // and start our media player.
                mediaPlayer.prepare()
                mediaPlayer.start()
                seekBar.max = mediaPlayer.getDuration()
                ib.setBackgroundResource(0)
                ib.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.pause_button
                    )
                )
                notifyItemChanged(aposition)
//                notifyItemChanged(aposition)
                thread = Thread(Runnable {
                    var currentPosition: Int = mediaPlayer.getCurrentPosition()
                    val total: Int = mediaPlayer.getDuration()
                    Log.i("totalPPP", "$currentPosition##$total")

                    while (mediaPlayer != null && mediaPlayer.isPlaying() ) {
                        currentPosition = try {
                            Log.i("ExceptioAudio", "try1")
                            Thread.sleep(1000)
                            mediaPlayer.getCurrentPosition()
                        } catch (e: InterruptedException) {
                            Log.i("ExceptioAudio", "$e#1")
                            return@Runnable
                        } catch (e: Exception) {
                            Log.i("ExceptioAudio", "$e#2")
                            return@Runnable
                        }
                        if(currentPosition==total){
                            seekBar.progress = 0
                            ib.setImageDrawable(
                                ContextCompat.getDrawable(
                                    context,
                                    R.drawable.play_button
                                )
                            )
                            break;
                        }else
                            seekBar.progress = currentPosition
                    }
                })
                thread!!.start()
            } catch (e: IOException) {
                e.printStackTrace()
                Log.d("ExceptioAudio", e.toString() + "")
            }
            // below line is use to display a toast message.
//            Toast.makeText(context, "Audio started playing..", Toast.LENGTH_SHORT).show()
            aposition = a_position
            flag=true;
            //notifyDataSetChanged()
        }
    }
    fun clearMediaPlayer() {
        mediaPlayer.stop()
        mediaPlayer.reset()
        mediaPlayer.release()
    }

}