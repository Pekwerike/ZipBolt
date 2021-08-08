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
        object Directory : File(9)

        sealed class Document(documentType: Int) : File(documentType) {
            object PdfDocument : Document(10)
            object WordDocument : Document(11)
            object ExcelDocument : Document(12)
            object UnknownDocument : Document(13)
            object PowerPointDocument : Document(14)
            object ZipDocument : Document(15)
            object WebpageDocument : Document(16)
            object DatDocument: Document(17)
        }
    }
}