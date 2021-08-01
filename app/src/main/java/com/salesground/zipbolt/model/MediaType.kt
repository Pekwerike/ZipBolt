package com.salesground.zipbolt.model

sealed class MediaType(val value: Int) {
    object Image : MediaType(1)
    object Video : MediaType(2)
    object Audio : MediaType(3)
    object App : MediaType(4)

    sealed class File(fileType: Int) : MediaType(fileType) {
        object ImageFile : File(5)
        object VideoFile : File(6)
        object AudioFile : File(7)
        object AppFile : File(8)
        object Directory: File(15)

        sealed class Document(documentType: Int) : File(documentType) {
            object PdfDocument : Document(9)
            object WordDocument : Document(10)
            object ExcelDocument : Document(11)
            object UnknownDocument : Document(12)
            object PowerPointDocument : Document(13)
            object ZipDocument : Document(14)
        }
    }
}