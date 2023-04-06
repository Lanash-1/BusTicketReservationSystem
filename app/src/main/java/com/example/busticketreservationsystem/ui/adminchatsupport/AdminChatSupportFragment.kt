package com.example.busticketreservationsystem.ui.adminchatsupport

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.busticketreservationsystem.R
import com.example.busticketreservationsystem.data.database.AppDatabase
import com.example.busticketreservationsystem.data.repository.AppRepositoryImpl
import com.example.busticketreservationsystem.databinding.FragmentAdminChatSupportBinding
import com.example.busticketreservationsystem.listeners.OnItemClickListener
import com.example.busticketreservationsystem.ui.adminservice.AdminServicesFragment
import com.example.busticketreservationsystem.ui.analytics.AnalyticsPageFragment
import com.example.busticketreservationsystem.ui.chat.ChatFragment
import com.example.busticketreservationsystem.viewmodel.livedata.AdminViewModel
import com.example.busticketreservationsystem.viewmodel.livedata.ChatViewModel
import com.example.busticketreservationsystem.viewmodel.viewmodelfactory.AdminViewModelFactory
import com.example.busticketreservationsystem.viewmodel.viewmodelfactory.ChatViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.transition.MaterialContainerTransform
import com.google.android.material.transition.MaterialElevationScale

class AdminChatSupportFragment : Fragment() {

    private lateinit var binding: FragmentAdminChatSupportBinding

    private lateinit var adminViewModel: AdminViewModel
    private lateinit var chatViewModel: ChatViewModel

    private val chatUserListAdapter = AdminChatSupportUserListAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(false)

        val database = AppDatabase.getDatabase(requireActivity().applicationContext)
        val repository = AppRepositoryImpl(database)

        val chatViewModelFactory = ChatViewModelFactory(repository)
        chatViewModel = ViewModelProvider(requireActivity(), chatViewModelFactory)[ChatViewModel::class.java]

        val adminViewModelFactory = AdminViewModelFactory(repository)
        adminViewModel = ViewModelProvider(requireActivity(), adminViewModelFactory)[AdminViewModel::class.java]

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity as AppCompatActivity).supportActionBar!!.apply {
            title = "Chat Box"
            setDisplayHomeAsUpEnabled(false)
        }
        binding = FragmentAdminChatSupportBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun backPressOperation() {
        parentFragmentManager.commit {
            setCustomAnimations(R.anim.from_left, R.anim.to_right)
//            replace(R.id.adminPanelFragmentContainer, AdminServicesFragment())
            replace(R.id.adminPanelFragmentContainer, AnalyticsPageFragment())
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().findViewById<BottomNavigationView>(R.id.admin_bottomNavigationView)?.visibility = View.VISIBLE

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true){
                override fun handleOnBackPressed() {
                    backPressOperation()
                    requireActivity().findViewById<BottomNavigationView>(R.id.admin_bottomNavigationView).selectedItemId = R.id.analytics
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        val userListRecyclerView = binding.chatUserListRecyclerView
        userListRecyclerView.adapter = chatUserListAdapter
        userListRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        chatViewModel.fetchUsersFromChat()

        chatViewModel.usersList.observe(viewLifecycleOwner, Observer{
            if(it.isNotEmpty()){
                chatUserListAdapter.setUserList(it)
                chatUserListAdapter.notifyDataSetChanged()
            }else{
                binding.emptyListLayout.visibility = View.VISIBLE
            }
        })

        chatUserListAdapter.setOnItemClickListener(object: OnItemClickListener{
            override fun onItemClick(position: Int) {
                adminViewModel.transitionPosition = position
                val fragment = ChatFragment()
                adminViewModel.chatUserId = chatViewModel.usersList.value!![position].userId
                parentFragmentManager.commit {
//                    val item = userListRecyclerView.findViewHolderForAdapterPosition(position)?.itemView
//                    item!!.transitionName = "chat_transition${position}"
//                    addSharedElement(item, "chat_transition${position}")
//                    fragment.sharedElementEnterTransition = MaterialContainerTransform().apply {
//                        duration = 500L
//                    }
                    setCustomAnimations(R.anim.from_right, R.anim.to_left)
                    replace(R.id.adminPanelFragmentContainer, fragment)
                }
            }
        })
    }
}