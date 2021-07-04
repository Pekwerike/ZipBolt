package com.salesground.zipbolt.ui.recyclerview.audioFragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.salesground.zipbolt.R
import com.salesground.zipbolt.databinding.VideoLayoutItemBinding
import com.salesground.zipbolt.model.DataToTransfer
import com.salesground.zipbolt.ui.recyclerview.DataToTransferRecyclerViewItemClickListener

/** AudioLayoutItemViewHolder is the view holder for each audio item displayed in the
 * AudioFragment Recyclerview. AudioLayoutItemViewHolder uses the R.layout.video_layout_item
 * since it shares similar UI structure with R.layout.video_layout_item
 * */
class AudioLayoutItemViewHolder(
    private val audioLayoutItemBinding: VideoLayoutItemBinding,
    private val dataToTransferRecyclerViewItemClickListener: DataToTransferRecyclerViewItemClickListener
) : RecyclerView.ViewHolder(audioLayoutItemBinding.root) {

    fun bindData(
        dataToTransfer: DataToTransfer,
        selectedAudios: MutableList<DataToTransfer>
    ) {
        dataToTransfer as DataToTransfer.DeviceAudio

        audioLayoutItemBinding.run {
            videoName = dataToTransfer.audioDisplayName
            videoSize = dataToTransfer.audioSize
            videoDuration = dataToTransfer.audioDuration

            Glide.with(videoLayoutItemVideoPreviewImageView)
                .load(dataToTransfer.audioArtPath)
                .error(R.drawable.ic_baseline_music_note_24)
                .into(videoLayoutItemVideoPreviewImageView)


            videoLayoutItemSelectableConstraintLayout.run {
                videoLayoutItemVideoSelectedCheckBox.setOnClickListener {
                    dataToTransferRecyclerViewItemClickListener.onClick(
                        dataToTransfer
                    )

                    if (selectedAudios.contains(dataToTransfer)) {
                        // user un-selected, so remove the video from the collection of selected videos
                        setIsViewSelected(false)
                        videoLayoutItemVideoSelectedCheckBox.isChecked = false
                        selectedAudios.remove(dataToTransfer)
                    } else {
                        // user selects, so add the application to the collection of selected videos
                        setIsViewSelected(true)
                        videoLayoutItemVideoSelectedCheckBox.isChecked = true
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
                        videoLayoutItemVideoSelectedCheckBox.isChecked = false
                        selectedAudios.remove(dataToTransfer)
                    } else {
                        // user selects, so add the application to the collection of selected videos
                        setIsViewSelected(true)
                        videoLayoutItemVideoSelectedCheckBox.isChecked = true
                        selectedAudios.add(dataToTransfer)
                    }
                }

                if (selectedAudios.contains(dataToTransfer)) {
                    setIsViewSelected(true)
                    videoLayoutItemVideoSelectedCheckBox.isChecked = true
                } else {
                    setIsViewSelected(false)
                    videoLayoutItemVideoSelectedCheckBox.isChecked = false
                }
            }

            executePendingBindings()
        }
    }

    companion object {
        fun createViewHolder(
            parent: ViewGroup,
            dataToTransferRecyclerViewItemClickListener: DataToTransferRecyclerViewItemClickListener
        ): AudioLayoutItemViewHolder {
            val layoutBinding = DataBindingUtil.inflate<VideoLayoutItemBinding>(
                LayoutInflater.from(parent.context),
                R.layout.video_layout_item,
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