package com.salesground.zipbolt.model

sealed class DocumentType(val value: Int) {
    object Image : DocumentType(1)
    object Video : DocumentType(2)
    object Audio : DocumentType(3)
    object App : DocumentType(4)
    object Directory : DocumentType(5)
    sealed class Document(private val documentValue: Int) : DocumentType(documentValue) {
        object Pdf : Document(6)
        object WordDocument : Document(7)
        object ExcelDocument : Document(8)
        object UnknownDocument : Document(9)
        object PowerPointDocument : Document(10)
        object ZipDocument: Document(11)
    }

}