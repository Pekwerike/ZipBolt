package com.salesground.zipbolt.model

import android.net.Uri

data class MediaModel (val imageUri : Uri,
                       val imageDisplayName : String,
                       val imageDateAdded : Long,
                       val imageSize : Long
)