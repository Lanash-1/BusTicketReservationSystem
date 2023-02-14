package com.example.busticketreservationsystem.ui.chat

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.busticketreservationsystem.R
import com.example.busticketreservationsystem.data.database.AppDatabase
import com.example.busticketreservationsystem.data.entity.Chat
import com.example.busticketreservationsystem.data.repository.AppRepositoryImpl
import com.example.busticketreservationsystem.databinding.FragmentChatBinding
import com.example.busticketreservationsystem.enums.AppUserType
import com.example.busticketreservationsystem.enums.LoginStatus
import com.example.busticketreservationsystem.enums.MessageType
import com.example.busticketreservationsystem.ui.adminchatsupport.AdminChatSupportFragment
import com.example.busticketreservationsystem.ui.homepage.HomePageFragment
import com.example.busticketreservationsystem.ui.user.UserDetailFragment
import com.example.busticketreservationsystem.utils.Helper
import com.example.busticketreservationsystem.viewmodel.LoginStatusViewModel
import com.example.busticketreservationsystem.viewmodel.NavigationViewModel
import com.example.busticketreservationsystem.viewmodel.livedata.AdminViewModel
import com.example.busticketreservationsystem.viewmodel.livedata.ChatViewModel
import com.example.busticketreservationsystem.viewmodel.livedata.UserViewModel
import com.example.busticketreservationsystem.viewmodel.viewmodelfactory.AdminViewModelFactory
import com.example.busticketreservationsystem.viewmodel.viewmodelfactory.ChatViewModelFactory
import com.example.busticketreservationsystem.viewmodel.viewmodelfactory.UserViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.delay


class ChatFragment : Fragment() {

    private lateinit var binding: FragmentChatBinding

    private val chatListAdapter = ChatMessageListAdapter()

    private val helper = Helper()

    private val navigationViewModel: NavigationViewModel by activityViewModels()
    private val loginStatusViewModel: LoginStatusViewModel by activityViewModels()
    private lateinit var chatViewModel: ChatViewModel
    private lateinit var userViewModel: UserViewModel
    private lateinit var adminViewModel: AdminViewModel


    private lateinit var chatList: List<Chat>

    private var chatUserId: Int = 0
    private lateinit var messageType: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        val database = AppDatabase.getDatabase(requireActivity().applicationContext)
        val repository = AppRepositoryImpl(database)

        val chatViewModelFactory = ChatViewModelFactory(repository)
        chatViewModel = ViewModelProvider(requireActivity(), chatViewModelFactory)[ChatViewModel::class.java]

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
            title = "Live Chat Support"
            setDisplayHomeAsUpEnabled(true)
        }


        // Inflate the layout for this fragment
        binding = FragmentChatBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if(loginStatusViewModel.status == LoginStatus.ADMIN_LOGGED_IN){
            inflater.inflate(R.menu.chat_menu, menu)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> {
                backPressOperation()
            }
            R.id.info_icon -> {
                navigationViewModel.adminNavigation = ChatFragment()
                adminViewModel.fetchUserDetails(adminViewModel.chatUserId)

                adminViewModel.isUserFetched.observe(viewLifecycleOwner, Observer{
                    if(it != null){
                        if(it){
                            parentFragmentManager.commit {
                                setCustomAnimations(R.anim.from_right, R.anim.to_left)
                                replace(R.id.adminPanelFragmentContainer, UserDetailFragment())
                            }
                        }
                    }
                })
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun backPressOperation() {
        chatViewModel.userChat.value?.clear()
        if(loginStatusViewModel.status == LoginStatus.ADMIN_LOGGED_IN){
            parentFragmentManager.commit {
                setCustomAnimations(R.anim.from_left, R.anim.to_right)
                replace(R.id.adminPanelFragmentContainer, AdminChatSupportFragment())
            }
        }else{
            parentFragmentManager.commit {
                setCustomAnimations(R.anim.from_left, R.anim.to_right)
                replace(R.id.main_fragment_container, HomePageFragment())
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.chatLayout.transitionName = "chat_transition${adminViewModel.transitionPosition}"


        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true){
                override fun handleOnBackPressed() {
                    backPressOperation()
                }
            }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        val chatRecyclerView = binding.chatRecyclerView
        chatRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        chatRecyclerView.adapter = chatListAdapter

        if(loginStatusViewModel.status == LoginStatus.ADMIN_LOGGED_IN){
            requireActivity().findViewById<BottomNavigationView>(R.id.admin_bottomNavigationView).visibility = View.GONE
            chatListAdapter.setUserType(AppUserType.ADMIN)
            messageType = MessageType.RECEIVED.name
            chatUserId = adminViewModel.chatUserId
            chatViewModel.fetchUserChat(adminViewModel.chatUserId)
        }else{
            requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility = View.GONE
            chatListAdapter.setUserType(AppUserType.CUSTOMER)
            chatUserId = userViewModel.user.userId
            messageType = MessageType.SENT.name
            chatViewModel.fetchUserChat(userViewModel.user.userId)
        }

        chatViewModel.userChat.observe(viewLifecycleOwner, Observer{
            binding.loaderIcon.visibility = View.GONE
            if(it.isNotEmpty()){
                chatList = it
                chatListAdapter.setMessageList(chatList)
                chatListAdapter.notifyDataSetChanged()
                chatRecyclerView.scrollToPosition(chatList.size - 1)
            }
        })

        binding.sendButton.setOnClickListener {
            val message = binding.enterMessageEditText.text.toString()
            if(helper.isValidMessage(message)){
                chatViewModel.newChat = Chat(0, chatUserId, message, helper.getTimeStamp(), messageType)
                chatViewModel.insertChat()
                binding.enterMessageEditText.setText("")
            }
        }

//        chatRecyclerView.scroll
    }

}