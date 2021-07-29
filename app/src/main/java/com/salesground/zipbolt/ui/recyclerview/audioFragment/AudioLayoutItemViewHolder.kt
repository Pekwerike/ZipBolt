package com.salesground.zipbolt.ui.recyclerview.audioFragment

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.setPadding
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.salesground.zipbolt.R
import com.salesground.zipbolt.databinding.AudioLayoutItemBinding
import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.ui.recyclerview.DataToTransferRecyclerViewItemClickListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/** AudioLayoutItemViewHolder is the view holder for each audio item displayed in the
 * AudioFragment Recyclerview. AudioLayoutItemViewHolder uses the R.layout.video_layout_item
 * since it shares similar UI structure with R.layout.video_layout_item
 * */
class AudioLayoutItemViewHolder(
    private val audioLayoutItemBinding: AudioLayoutItemBinding,
    private val dataToTransferRecyclerViewItemClickListener: DataToTransferRecyclerViewItemClickListener<DataToTransfer>
) : RecyclerView.ViewHolder(audioLayoutItemBinding.root) {

    fun bindData(
        dataToTransfer: DataToTransfer,
        selectedAudios: MutableList<DataToTransfer>
    ) {
        dataToTransfer as DataToTransfer.DeviceAudio

        audioLayoutItemBinding.run {
            audioName = dataToTransfer.audioDisplayName
            audioSize = dataToTransfer.audioSize
            audioDuration = dataToTransfer.audioDuration

            Glide.with(audioLayoutItemVideoPreviewImageView)
                .load(dataToTransfer.audioArtPath)
                .error(R.drawable.ic_baseline_music_note_24)
                .into(audioLayoutItemVideoPreviewImageView)



            audioLayoutItemSelectableLinearLayout.run {
                audioLayoutItemVideoSelectedCheckBox.setOnClickListener {
                    dataToTransferRecyclerViewItemClickListener.onClick(
                        dataToTransfer
                    )

                    if (selectedAudios.contains(dataToTransfer)) {
                        // user un-selected, so remove the video from the collection of selected videos
                        setIsViewSelected(false)
                        audioLayoutItemVideoSelectedCheckBox.isChecked = false
                        selectedAudios.remove(dataToTransfer)
                    } else {
                        // user selects, so add the application to the collection of selected videos
                        setIsViewSelected(true)
                        audioLayoutItemVideoSelectedCheckBox.isChecked = true
                        selectedAudios.add(dataToTransfer)
                    }
                }

                setOnClickListener {
                    dataToTransferRecyclerViewItemClickListener.onClick(
                        dataToTransfer
                    )

                    if (selectedAudios.contains(dataToTransfer)) {
                        // user un-selected, so remove the video from the collection of selected videos
                        setIsViewSelected(false)
                        audioLayoutItemVideoSelectedCheckBox.isChecked = false
                        selectedAudios.remove(dataToTransfer)
                    } else {
                        // user selects, so add the application to the collection of selected videos
                        setIsViewSelected(true)
                        audioLayoutItemVideoSelectedCheckBox.isChecked = true
                        selectedAudios.add(dataToTransfer)
                    }
                }

                if (selectedAudios.contains(dataToTransfer)) {
                    setIsViewSelected(true)
                    audioLayoutItemVideoSelectedCheckBox.isChecked = true
                } else {
                    setIsViewSelected(false)
                    audioLayoutItemVideoSelectedCheckBox.isChecked = false
                }
            }

            executePendingBindings()
        }
    }

    companion object {
        fun createViewHolder(
            parent: ViewGroup,
            dataToTransferRecyclerViewItemClickListener: DataToTransferRecyclerViewItemClickListener<DataToTransfer>
        ): AudioLayoutItemViewHolder {
            val layoutBinding = DataBindingUtil.inflate<AudioLayoutItemBinding>(
                LayoutInflater.from(parent.context),
                R.layout.audio_layout_item,
                parent,
                false
            )

            return AudioLayoutItemViewHolder(
                layoutBinding,
                dataToTransferRecyclerViewItemClickListener
            )
        }
    }
}