package com.example.busticketreservationsystem.ui.user

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.busticketreservationsystem.R
import com.example.busticketreservationsystem.data.database.AppDatabase
import com.example.busticketreservationsystem.data.entity.User
import com.example.busticketreservationsystem.data.repository.AppRepositoryImpl
import com.example.busticketreservationsystem.databinding.FragmentUserDetailBinding
import com.example.busticketreservationsystem.enums.Gender
import com.example.busticketreservationsystem.enums.LoginStatus
import com.example.busticketreservationsystem.ui.chat.ChatFragment
import com.example.busticketreservationsystem.ui.editprofile.EditProfileFragment
import com.example.busticketreservationsystem.ui.myaccount.MyAccountFragment
import com.example.busticketreservationsystem.viewmodel.LoginStatusViewModel
import com.example.busticketreservationsystem.viewmodel.NavigationViewModel
import com.example.busticketreservationsystem.viewmodel.livedata.AdminViewModel
import com.example.busticketreservationsystem.viewmodel.livedata.UserViewModel
import com.example.busticketreservationsystem.viewmodel.viewmodelfactory.AdminViewModelFactory
import com.example.busticketreservationsystem.viewmodel.viewmodelfactory.UserViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView

class UserDetailFragment : Fragment() {

    private lateinit var binding: FragmentUserDetailBinding

    private lateinit var adminViewModel: AdminViewModel
    private lateinit var userViewModel: UserViewModel
    private val navigationViewModel: NavigationViewModel by activityViewModels()
    private val loginStatusViewModel: LoginStatusViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        val database = AppDatabase.getDatabase(requireActivity().applicationContext)
        val repository = AppRepositoryImpl(database)

        val adminViewModelFactory = AdminViewModelFactory(repository)
        adminViewModel = ViewModelProvider(requireActivity(), adminViewModelFactory)[AdminViewModel::class.java]

        val userViewModelFactory = UserViewModelFactory(repository)
        userViewModel = ViewModelProvider(requireActivity(), userViewModelFactory)[UserViewModel::class.java]

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        (activity as AppCompatActivity).supportActionBar!!.apply {
            title = if(loginStatusViewModel.status == LoginStatus.LOGGED_IN){
                getString(R.string.profile_details)
            }else{
                getString(R.string.user_details)
            }
            setDisplayHomeAsUpEnabled(true)
        }

        binding = FragmentUserDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if(loginStatusViewModel.status == LoginStatus.LOGGED_IN){
            inflater.inflate(R.menu.edit_menu, menu)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> {
                backPressOperation()
            }
            R.id.edit_icon -> {
                moveToNextFragment(R.id.homePageFragmentContainer, EditProfileFragment())
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun backPressOperation() {
        if(loginStatusViewModel.status == LoginStatus.LOGGED_IN){
            moveToPreviousFragment(R.id.homePageFragmentContainer, MyAccountFragment())
        }else{
            when(navigationViewModel.adminNavigation){
                is ChatFragment -> {
                    navigationViewModel.adminNavigation = null
                    moveToPreviousFragment(R.id.adminPanelFragmentContainer, ChatFragment())
                }
                else -> {
                    moveToPreviousFragment(R.id.adminPanelFragmentContainer, UserListFragment())
                }
            }
        }
    }

    private fun moveToPreviousFragment(fragmentContainer: Int, fragment: Fragment) {
        parentFragmentManager.commit {
            setCustomAnimations(R.anim.from_left, R.anim.to_right)
            replace(fragmentContainer, fragment)
        }
    }

    private fun moveToNextFragment(fragmentContainer: Int, fragment: Fragment){
        parentFragmentManager.commit {
            setCustomAnimations(R.anim.from_right, R.anim.to_left)
            replace(fragmentContainer, fragment)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().findViewById<BottomNavigationView>(R.id.admin_bottomNavigationView)?.visibility = View.GONE
        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)?.visibility = View.GONE

        adminViewModel.isUserFetched.value = null

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true){
                override fun handleOnBackPressed() {
                    backPressOperation()
                }
            }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        if(loginStatusViewModel.status == LoginStatus.LOGGED_IN){

            setUserDetailsToView(userViewModel.user)

//            userViewModel.fetchUserTicketCount(userViewModel.user.userId)

//            userViewModel.cancelledCount.observe(viewLifecycleOwner, Observer{
//                setTicketCountDataToView(userViewModel.upcomingCount, userViewModel.completedCount, userViewModel.cancelledCount.value!!)
//            })


        }else{
            setUserDetailsToView(adminViewModel.selectedUser)

//            adminViewModel.fetchUserTicketCount(adminViewModel.selectedUser.userId)

//            adminViewModel.cancelledCount.observe(viewLifecycleOwner, Observer{
////                setTicketCountDataToView(adminViewModel.upcomingCount, adminViewModel.completedCount, adminViewModel.cancelledCount.value!!)
//            })
        }
    }

//    private fun setTicketCountDataToView(upcomingCount: Int, completedCount: Int, cancelledCount: Int) {
//            binding.apply {
//                if(upcomingCount < 10){
//                    upcomingCountTextView.text = "0$upcomingCount"
//                }
//                if(completedCount < 10){
//                    completedCountTextView.text = "0$completedCount"
//                }
//                if(cancelledCount < 10){
//                    cancelledCountTextView.text = "0${cancelledCount}"
//                }
//        }
//    }

    private fun setUserDetailsToView(user: User) {
        with(adminViewModel){
            binding.apply {

                if(user.username.isNotEmpty()){
                    usernameInputTextView.text = user.username
                }else{
                    usernameInputTextView.text = " - "
                }

                if(user.gender.isNotEmpty()){
                    if(user.gender == Gender.MALE.name){
                        genderInputTextView.text = getString(R.string.male)
                    }else{
                        genderInputTextView.text = getString(R.string.female)
                    }
                }else{
                    genderInputTextView.text = " - "
                }

                if(user.dob.isNotEmpty()){
                    dobInputTextView.text = user.dob
                }else{
                    dobInputTextView.text = " - "
                }

                if(user.emailId.isNotEmpty()){
                    mailInputTextView.text = user.emailId
                }else{
                    mailInputTextView.text = " - "
                }

                if(user.mobileNumber.isNotEmpty()){
                    mobileInputTextView.text = user.mobileNumber
                }else{
                    mobileInputTextView.text = " - "
                }

            }
        }
    }
}


//binding.imageView.setOnClickListener{
//    val intent = Intent(Intent.ACTION_PICK)
//    intent.type = "image/*"
//    startActivityForResult(intent, 1)
//}
//
//
//
//override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//    super.onActivityResult(requestCode, resultCode, data)
//
//    if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
//        val imageUri = data?.data
//        println("IMAGE URI = $imageUri")
//        val bitmap = imageUri?.let {
//            getBitmapFromUri(requireContext(), it)
//        }
//        loginStatusViewModel.bitmapValue = bitmap
//        println("BITMAP = $bitmap")
////            binding.imageView.setImageURI(imageUri)
//        binding.imageView.setImageBitmap(bitmap)
//    }
//}
//
//private fun getBitmapFromUri(context: Context, uri: Uri): Bitmap? {
//    val parcelFileDescriptor = context.contentResolver.openFileDescriptor(uri, "r")
//    val fileDescriptor = parcelFileDescriptor?.fileDescriptor
//    val bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor)
//    parcelFileDescriptor?.close()
//    return bitmap
//}
