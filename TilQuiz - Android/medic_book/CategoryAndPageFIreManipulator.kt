package madortil.medicBook

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.fragment.app.Fragment

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import madortil.medicBook.models.category
import madortil.medicBook.models.keys

import java.util.HashMap

/**
 * Created by danfechtmann on 11/03/2018.
 */

class CategoryAndPageFIreManipulator(var context: Context) {

    //Create a new page, download a sample pdf, reload it with a new name, derived from the uid of the page +.pdf extension
    fun createNewPage(categoryKey: String?, fragment: Fragment) {
        val newPage = HashMap<String, Any>()

        val dbRef = FirebaseDatabase.getInstance().reference.child(keys.BOOK_GROUP).child(categoryKey!!).child(keys.PAGES).push()
        newPage[keys.NAME] = "עמוד חדש"
        val newUrl = dbRef.key!! + ".pdf"
        newPage[keys.URL] = newUrl
        dbRef.updateChildren(newPage).addOnCompleteListener { task ->
            //once the page is created, download a sample pdf
            if (task.isSuccessful) {

                val storageRef = FirebaseStorage.getInstance().reference
                storageRef.child("nana.pdf").getBytes(999999999).addOnCompleteListener { taskDownload ->
                    //if the download of the sample pdf is successful
                    if (taskDownload.isSuccessful) {
                        //reload with new url
                        storageRef.child(newUrl).putBytes(taskDownload.result!!).addOnCompleteListener { taskUpload ->
                            //if the new file uploaded successfully
                            if (taskUpload.isSuccessful) {
                                showText("עמוד חדש נוצר בהצלחה")
                                DataHolder.updateAdapters()
                                fragment.fragmentManager?.beginTransaction()?.detach(fragment)?.attach(fragment)?.commit()
                            } else {
                                showText("קרתה שגיעה")
                            }//if the reload is unsuccessful
                        }
                    } else {
                        showText("קרתה שגיעה")
                    }//if the download is not successful
                }
            } else {
                showText("קרתה שגיעה")
            }//if an error occurred during the creation
        }
    }

    fun deletePage(fragment: Fragment, pageIndex: Int, cat: category) {
        val catKey = cat.key
        val pageKey = cat.pages[pageIndex].pageKey
        val pdfUrl = cat.pages[pageIndex].url

        FirebaseStorage.getInstance().reference.child(pdfUrl).delete().addOnCompleteListener { task ->
            if (task.isComplete) {
                //if the deletion was successful move to deleting the database data
                FirebaseDatabase.getInstance().reference.child(keys.BOOK_GROUP).child(catKey).child(keys.PAGES).child(pageKey).setValue(null)
                restartFragment(fragment)
            }
        }
    }

    private fun restartFragment(fragment: Fragment) {
        DataHolder.updateAdapters()
        fragment.fragmentManager!!.beginTransaction().detach(fragment).attach(fragment).commit()
    }

    fun deleteCategory(fragment: Fragment, cat: category) {
        val dbRef = FirebaseDatabase.getInstance().reference.child(keys.BOOK_GROUP).child(cat.key)
        dbRef.child(keys.PAGES).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.childrenCount > 0) {
                    Toast.makeText(context, "לא ניתן למחוק קטגוריה עם עמודים, מחקו את העמודים קודם", Toast.LENGTH_LONG).show()
                } else {
                    dbRef.setValue(null)
                }
                restartFragment(fragment)
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

    fun createCategory(fragment: Fragment) {
        val dbRef = FirebaseDatabase.getInstance().reference.child(keys.BOOK_GROUP).push()
        val catKey = dbRef.key
        val map = HashMap<String, Any>()
        map[keys.NAME] = "קטגוריה חדשה"
        dbRef.updateChildren(map)
        createNewPage(catKey, fragment)
    }

    private fun showText(text: String) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }


}
