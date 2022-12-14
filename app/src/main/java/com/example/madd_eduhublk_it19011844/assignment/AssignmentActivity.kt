//IT19011844-Hemachandra M.G.S.P.- Assignment Component
package com.example.madd_eduhublk_it19011844.assignment
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.madd_eduhublk_it19011844.Class.Assignment
import com.example.madd_eduhublk_it19011844.HomepageActivity
import com.example.madd_eduhublk_it19011844.MainActivity
import com.example.madd_eduhublk_it19011844.MainActivity.Companion.classId
import com.example.madd_eduhublk_it19011844.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_assignment.*
import kotlinx.android.synthetic.main.single_assignment_layout.view.*
class AssignmentActivity : AppCompatActivity() {

    private val currentUser by lazy { FirebaseAuth.getInstance().currentUser }
    private val mRootRef = FirebaseDatabase.getInstance().reference

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_assignment)

        if (currentUser == null){
            //sendToHomepage()
            return
        }

        initialize()

    }
private fun initialize() {
    title = "Assignment"
    supportActionBar?.setDisplayShowHomeEnabled(true)
    assignment_list.setHasFixedSize(true)
    assignment_list.layoutManager = LinearLayoutManager(this)
    initializeFloatingButton()
}
private fun initializeFloatingButton() {
    mRootRef.child("Class-Enroll/${currentUser!!.uid}/$classId/as").addValueEventListener(object : ValueEventListener{

        override fun onCancelled(p0: DatabaseError) {

            Log.d(TAG, "Error : ${p0.message}")
        }
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            if(dataSnapshot.value == "teacher"){
                assignment_upload_button.show()
                assignment_upload_button.setOnClickListener {
                    sendToAssignmentUploadActivity()
                }
            }else{
                assignment_upload_button.hide()
            }
        }
    })
}
override fun onStart() {
        super.onStart()

        if(classId == "null"){
            sendToMainActivity()
            return
        }

        val assignmentList = ArrayList<Assignment>()

        val assignmentAdapter = object :RecyclerView.Adapter<AssignmentViewHolder>(){
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssignmentViewHolder {
                //                Log.d(TAG,"View Type: ${viewType.toString()}")

                return AssignmentViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.single_assignment_layout, parent, false))
            }

            override fun getItemCount() = assignmentList.size

            override fun onBindViewHolder(holder: AssignmentViewHolder, position: Int) {
                holder.bind(assignmentList[position])
                holder.view.setOnClickListener {
                    sendToAssignmentDetails(assignmentList[position].uploadingDate)
                }
            }

        }

        mRootRef.child("Assignment/$classId").addValueEventListener(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                Log.d("chetan", "Database Reference for Assignment is on cancelled, ${p0.message}")
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                assignmentList.clear()

                for(assignmentDataSnapshot in dataSnapshot.children){
                    if(assignmentDataSnapshot == null)continue

                    val assignment = Assignment(
                            title = assignmentDataSnapshot.child("title").value.toString(),
                            description = assignmentDataSnapshot.child("description").value.toString(),
                            submissionDate = assignmentDataSnapshot.child("submissionDate").value.toString(),
                            uploadingDate = assignmentDataSnapshot.key.toString()
                    )
                    assignmentList.add(assignment)
                }

                if(assignmentList.size == 0) {
                    assignment_empty.visibility = View.VISIBLE
                    assignment_empty.visibility = View.GONE
                }
                else {
                    assignment_empty.visibility = View.GONE
                    assignment_list.visibility = View.VISIBLE
                    assignment_list.adapter = assignmentAdapter
                }
            }
        })
    }

    private fun sendToAssignmentDetails(assignment: String?) {
        if (classId == "null") {
            sendToMainActivity()
            return
        }

        if (assignment == null){
            return
        }

        val intent = Intent(this,AssignmentDetailsActivity::class.java)
        intent.putExtra("assignment",assignment)
        startActivity(intent)
    }

    private fun sendToAssignmentUploadActivity() {
        if (classId == "null") {
            sendToMainActivity()
            return
        }

        startActivity(Intent(this,AssignmentUploadActivity::class.java))
    }

    private fun sendToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun sendToHomepage(): FirebaseUser? {
        val intent = Intent(this, HomepageActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
        return null
    }


    private class AssignmentViewHolder(val view: View):RecyclerView.ViewHolder(view){
        fun bind(assignment: Assignment) {

//            Log.d(TAG,"ClassViewHolder")
            Log.d(TAG, "title : ${assignment.title}")
//            Log.d(TAG,"description : ${assignment.description}")
//            Log.d(TAG,"link : ${assignment.link}")
//            Log.d(TAG,"submission : ${assignment.submissionDate}")
//            Log.d(TAG,"maxMarks : ${assignment.maxMarks}")

            with(assignment) {
                setTitle(this.title)
                setDescription(this.description)
                setSubmissionDate(this.submissionDate)
            }
        }

        private fun setTitle(string:String?){

            view.single_assignment_title.text = when(string){
                "null" -> null
                else -> string
            }
        }

        private fun setDescription(string:String?){
            view.single_assignment_description.text = when(string){
                "null" -> null
                else -> string
            }
        }

        private fun setSubmissionDate(string:String?){
            view.single_assignment_submission_date.text = when(string){
                "null" -> null
                else -> string
            }
        }
    }

    companion object {
        const val TAG = "Assignment_Activity"
    }


}