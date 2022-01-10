package madortil.medicBook.viewControllers


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_admin_login.*
import kotlinx.android.synthetic.main.fragment_admin_login.backgroundImageView
import madortil.medicBook.DataHolder
import com.madortilofficialapps.tilquiz.R
import madortil.medicBook.models.HourPeriod
import madortil.medicBook.models.hourPeriod
import madortil.medicBook.models.keys

/**
 * A simple [Fragment] subclass.
 *
 */
class AdminLoginFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_admin_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpUI()
    }

    private fun setUpUI() {
        backgroundImageView.setImageResource(
                when (hourPeriod) {
                    HourPeriod.Morning -> R.drawable.ic_screen_background_morningr
                    HourPeriod.Noon -> R.drawable.ic_screen_background_noonr
                    else -> R.drawable.ic_screen_background_nightr
                })
        passbutton.setOnClickListener {
            FirebaseDatabase.getInstance().reference.child(keys.PASSCODE).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val currentPass = passtext.text.toString()
                    val realPass = dataSnapshot.value!!.toString()
                    if (currentPass == realPass) {
                        DataHolder.isAdmin = true
                        Toast.makeText(context, "אתה מנהל!", Toast.LENGTH_SHORT).show()
                        continueToShowCategories()
                    } else {
                        Toast.makeText(context, "אופס! הסיסמא לא נכונה", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(context, "קרתה שגיאה, נסה שנית", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun continueToShowCategories() {
        findNavController().navigate(
                AdminLoginFragmentDirections.actionAdminLoginFragmentToShowCategoriesFragment()
        )
    }


}
