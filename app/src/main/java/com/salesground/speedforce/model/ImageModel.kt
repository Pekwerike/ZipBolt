package com.salesground.speedforce.model

import android.net.Uri

data class ImageModel (val imageUri : Uri,
                       val imageDisplayName : String,
                       val imageDateAdded : Long,
                       val imageSize : Long
)