package madortil.medicBook.models

/**
 * Created by danfechtmann on 07/03/2018.
 */

class PDFCheckResult(var pdfCheckResultStatus: PDFCheckResultStatus, var file: pdfFile?) {
    enum class PDFCheckResultStatus {
        Success,
        Fail
    }
}

