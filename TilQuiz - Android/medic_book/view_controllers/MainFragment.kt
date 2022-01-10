package madortil.medicBook.viewControllers


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_main.*
import madortil.medicBook.DataHolder
import com.madortilofficialapps.tilquiz.R
import madortil.medicBook.models.HourPeriod
import madortil.medicBook.models.category
import madortil.medicBook.models.hourPeriod
import madortil.medicBook.models.keys
import java.util.ArrayList

/**
 * A simple [Fragment] subclass.
 *
 */
class MainFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUIElements()
        setUIElementsListeners()
        anonymousLogin()

        activity!!.title = "מדיקבוק"
    }
    private fun setUIElements() {
        backgroundImageView.setImageResource(
                when (hourPeriod) {
                    HourPeriod.Morning -> R.drawable.ic_screen_background_morningm
                    HourPeriod.Noon -> R.drawable.ic_screen_background_noonm
                    else -> R.drawable.ic_screen_background_nightm
                })
        textviewCred.text = "מהנדס תוכנה משני - דן פכטמן \n מדור ט׳יל, בה׳ד 10"
    }

    private fun setUIElementsListeners() {
        adminLogin.setOnClickListener {
            findNavController().navigate(MainFragmentDirections.actionMainFragmentToAdminLoginFragment())
        }
        userLogin.setOnClickListener {
            findNavController().navigate(MainFragmentDirections.actionMainFragmentToShowCategoriesFragment())
        }
    }

    private fun changeControlIntractability(status: Boolean?) {
        adminLogin?.isEnabled = status!!
        userLogin?.isEnabled = status
    }

    private fun setButtonOpacity(opacity: Float) {
        adminLogin?.alpha = opacity
        userLogin?.alpha = opacity
    }

    private fun setButtonInteraction(status: Boolean) {
        //interaction enabled
        if (status) {
            textview_loadingText?.visibility = View.INVISIBLE
            readingDatabaseProgress?.visibility = View.INVISIBLE
            changeControlIntractability(status)
            setButtonOpacity(1.0f)
        } else {
            textview_loadingText?.visibility = View.VISIBLE
            readingDatabaseProgress?.visibility = View.VISIBLE
            changeControlIntractability(status)
            setButtonOpacity(0.5f)
        }//interaction disabled
    }

    private fun readBooksAndPages() {
        DataHolder.categories = ArrayList()
        FirebaseDatabase.getInstance().reference.child(keys.BOOK_GROUP).addValueEventListener(object : ValueEventListener {
            var idCounter = 0
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                DataHolder.categories!!.clear()
                for (snap in dataSnapshot.children) {
                    val catKey = snap.key
                    val catName = snap.child(keys.NAME).value!!.toString()
                    val newCat = category(catKey!!, catName, idCounter)

                    for (page in snap.child(keys.PAGES).children) {
                        idCounter++
                        val pgeName = page.child(keys.NAME).value!!.toString()
                        val pgeURL = page.child(keys.URL).value!!.toString()
                        val pgeKey = page.key!!.toString()
                        newCat.pages.add(madortil.medicBook.models.page(pgeName, pgeURL, idCounter, pgeKey, catKey))
                    }
                    idCounter++
                    DataHolder.categories!!.add(newCat)
                }
                DataHolder.categories!!.sortBy { it.name }
                for (index in DataHolder.categories!!.indices) {
                    DataHolder.categories!![index].pages.sortBy { it.name }
                }

                setButtonInteraction(true)
                DataHolder.updateAdapters()
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

    private fun anonymousLogin() {
        setButtonInteraction(false)
        val auth = FirebaseAuth.getInstance()
        auth.signInAnonymously().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("result", "success")
                readBooksAndPages()
            } else {
                Log.d("result", "isn't successful")
                setButtonInteraction(true)
            }
        }
    }
}
