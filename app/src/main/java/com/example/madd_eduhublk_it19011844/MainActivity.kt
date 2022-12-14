//IT19011844-Hemachandra M.G.S.P.- Assignment Component
package com.example.madd_eduhublk_it19011844

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.madd_eduhublk_it19011844.Class.ClassAttribute
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.single_classroom_layout.view.*
import android.view.MenuItem as MenuItem1

class MainActivity : AppCompatActivity() {

    private val mCurrentUser: FirebaseUser? by lazy { FirebaseAuth.getInstance().currentUser }

    private val mRootRef = FirebaseDatabase.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(mCurrentUser == null){
            sendToHomepage()
            return
        }


        main_class_list.setHasFixedSize(true)
        main_class_list.layoutManager = LinearLayoutManager(this)

        main_create_class.setOnClickListener { sendToCreateClassActivity() }

        //mClassroomReference.keepSynced(true)
        //mClassEnrollReference.keepSynced(true)
    }

    override fun onStart() {
        super.onStart()

        if(mCurrentUser == null){
            sendToHomepage()
            return
        }

        mRootRef.child("Users/${mCurrentUser!!.uid}/register").addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                Log.d(TAG, "error : ${p0.message}")
            }
        })

        val classList = ArrayList<ArrayList<String>>()

        val classListAdapter = object : RecyclerView.Adapter<ClassViewHolder>(){
            override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ClassViewHolder {
                return ClassViewHolder(LayoutInflater.from(p0.context)
                        .inflate(R.layout.single_classroom_layout, p0, false), applicationContext)
            }

            override fun getItemCount() = classList.size

            override fun onBindViewHolder(holder: ClassViewHolder, p: Int) {
                Log.d(TAG,"Class Adapter onBind : $classList[")
                holder.bind(classList[p])
                holder.view.setOnClickListener{
                    //sendToClassHomeActivity(classList[p][0])
                }
            }
        }

        mRootRef.child("Class-Enroll/${mCurrentUser!!.uid}").addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
//                Toast.makeText(this@MainActivity,"Error : ${p0.message}",Toast.LENGTH_SHORT).show()
                Log.d(TAG,"class-enroll on canceled ${p0.message}")
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                classList.clear()

                for(group in dataSnapshot.children){
                    if (group.value == null)
                        continue

                    val list = ArrayList<String>()
                    list.add(group.key.toString())
                    list.add(group.child("as").value.toString())

                    classList.add(list)
                }
                main_class_list.adapter = classListAdapter
            }

        })

    }

    private fun sendToCreateClassActivity() {
        startActivity(Intent(this, CreateClassActivity::class.java))
    }

    private fun sendToHomepage() {
        startActivity(Intent(this, HomepageActivity::class.java))
        finish()
    }

    //Menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)

        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem1?): Boolean {

        Log.d("chetan", "${item?.itemId} is clicked")

        item ?: return false

        when (item.itemId){
            R.id.main_setting_btn -> {
                startActivity(Intent(this, UserProfileActivity::class.java))
            }

            R.id.main_logout_btn -> {
                FirebaseAuth.getInstance().signOut()
                sendToHomepage()
            }

            R.id.main_join_class_btn -> {
                startActivity(Intent(this,JoinClass::class.java))
            }
        }
        return true
    }

    private class ClassViewHolder(val view: View, val ctx :Context) : RecyclerView.ViewHolder(view) {

        fun bind(list:ArrayList<String>){

            setVisibility(false)

            FirebaseDatabase.getInstance().getReference("Classroom/${list[0]}").addValueEventListener(object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {
                    Log.d(TAG,"Error : ${p0.message}")
                }

                override fun onDataChange(data: DataSnapshot) {
                    val classAttribute = ClassAttribute()
                    classAttribute.id = list[0]
                    classAttribute.registeredAs = list[1]
                    classAttribute.status = data.child("status").value.toString()
                    classAttribute.profileImage = data.child("thumbImage").value?.toString() ?: data.child("image").value.toString()
                    classAttribute.name = data.child("name").value.toString()


                    setVisibility(true)
                    bind(classAttribute)
                }
            })
        }

        fun bind(_class: ClassAttribute) {
            Log.d("chetan", "ClassViewHolder")
            Log.d("chetan", "classid : ${_class.name}")
            Log.d("chetan", "classid : ${_class.id}")
            Log.d("chetan", "classid : ${_class.profileImage}")
            Log.d("chetan", "classid : ${_class.status}")
            with(_class) {
//                setBackground(this.registeredAs)
                setName(this.name)
                setStatus(this.status)
                setProfileImage(this.profileImage)
                setRegisteredAs(this.registeredAs)

            }
        }

        private fun setVisibility(enable:Boolean){
            view.visibility = when(enable){true->View.VISIBLE; else->View.GONE}
        }

        private fun setBackground(registeredAs:String){

            val gd = GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, intArrayOf(Color.parseColor("#ffffff")
                    ,Color.parseColor("#ffffff")
                    ,Color.parseColor("#ffffff")
                    ,when(registeredAs){"teacher" -> Color.parseColor("#FFA631"); else -> Color.parseColor("#00FFFF") }
            ))
            gd.cornerRadius = 0f
            view.background = gd
        }

        private fun setName(name: String) {
            view.class_single_name.text = name
        }

        private fun setStatus(status: String) {
            view.class_single_status.text = status
        }

        private fun setProfileImage(image: String) {
            Log.d(TAG, "image mian : $image")
            val glideImage:Any = when(image){"default","null", "" -> R.drawable.ic_classroom else -> image}
            Glide.with(ctx).load(glideImage).into(view.class_single_image)
        }

        private fun setRegisteredAs(registeredAs:String){
            val glideImage:Any = when(registeredAs){"teacher" -> R.drawable.ic_teacher else -> R.drawable.ic_student}
            Glide.with(ctx).load(glideImage).into(view.class_single_registered_as)
        }
    }

    companion object {
        private const val TAG = "chetan"
        var  classId : String = "null"
    }


}