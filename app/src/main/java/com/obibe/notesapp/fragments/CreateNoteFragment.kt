package com.obibe.notesapp.fragments

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.obibe.notesapp.R
import com.obibe.notesapp.database.NotesDatabase
import com.obibe.notesapp.entities.Notes
import com.obibe.notesapp.utils.showToast
import kotlinx.android.synthetic.main.fragment_bottom_sheet.*
import kotlinx.android.synthetic.main.fragment_create_note.*
import kotlinx.android.synthetic.main.fragment_create_note.layoutImage
import kotlinx.android.synthetic.main.fragment_create_note.layoutWebUrl
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.item_note.*
import kotlinx.coroutines.launch
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*


class CreateNoteFragment : BaseFragment(), EasyPermissions.PermissionCallbacks, EasyPermissions.RationaleCallbacks {

    var selectedColor: String = "#171C26"
    var currentDate: String? = null
    private var READ_STORAGE_PERM = 123
    private var REQUEST_CODE_IMAGE = 456
    private var selectedImagePath = ""
    private var webLink = ""
    private var noteId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        noteId = requireArguments().getInt("noteId", -1)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_note, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            CreateNoteFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (noteId != -1) {
            launch {
                context?.let {
                    var notes = NotesDatabase.getDatabase(it).noteDao().getSpecificNote(noteId)
                    selectedColor = notes.color!!

                    color_view.setBackgroundColor(Color.parseColor(notes.color))
                    add_note_title.setText(notes.title)
                    add_note_subtitle.setText(notes.subTitle)
                    add_note_desc.setText(notes.noteText)

                    if (notes.imgPath != "") {
                        selectedImagePath = notes.imgPath!!
                        img_view.setImageBitmap(BitmapFactory.decodeFile(notes.imgPath))
                        layoutImage.visibility = View.VISIBLE
                        img_view.visibility = View.VISIBLE
                        img_delete.visibility = View.VISIBLE
                    } else {
                        layoutImage.visibility = View.GONE
                        img_view.visibility = View.GONE
                        img_delete.visibility = View.GONE
                    }

                    if (notes.webLink != "") {
                        webLink = notes.webLink!!
                        tvWebLink.text = notes.webLink
                        layoutWebUrl.visibility = View.VISIBLE
                        webUrl_et.setText(notes.webLink)
                        imgUrlDelete.visibility = View.VISIBLE
                    } else {
                        imgUrlDelete.visibility = View.GONE
                        layoutWebUrl.visibility = View.GONE
                    }
                }
            }
        }

        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
            BroadcastReceiver, IntentFilter("bottom_sheet_action")
        )

        val date = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
        currentDate = date.format(Date())

        date_time.text = currentDate

        confirm_btn.setOnClickListener {

            if (noteId != -1) {
                updateNote()
            } else {
                addNote()
            }
        }

        back_btn.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        bar_btn.setOnClickListener {
            var bottomSheetFragment = BottomSheetFragment.newInstance(noteId)
            bottomSheetFragment.show(requireActivity().supportFragmentManager, "Bottom Sheet Fragment")
        }

        btnOk.setOnClickListener {
            if (webUrl_et.text.toString().trim().isNotEmpty()) {
                checkWebUrl()
            } else {
                showToast("Url is Required")
            }
        }

        btnCancel.setOnClickListener {
            if (noteId != -1) {
                tvWebLink.visibility = View.VISIBLE
                layoutWebUrl.visibility = View.GONE
            } else {
                layoutWebUrl.visibility = View.GONE
            }
        }

        tvWebLink.setOnClickListener {
            var intent = Intent(Intent.ACTION_VIEW, Uri.parse(webUrl_et.text.toString()))
            startActivity(intent)
        }

        img_delete.setOnClickListener {
            selectedImagePath = ""
            layoutImage.visibility = View.GONE
        }

        imgUrlDelete.setOnClickListener {
            webLink = ""
            tvWebLink.visibility = View.GONE
            imgUrlDelete.visibility = View.GONE
            layoutWebUrl.visibility = View.GONE
        }
    }

    private fun updateNote() {
        launch {

            context?.let {
               var notes = NotesDatabase.getDatabase(it).noteDao().getSpecificNote(noteId)

                notes.title = add_note_title.text.toString()
                notes.subTitle = add_note_subtitle.text.toString()
                notes.noteText = add_note_desc.text.toString()
                notes.dateTime = currentDate
                notes.color = selectedColor
                notes.imgPath = selectedImagePath
                notes.webLink = webLink

                NotesDatabase.getDatabase(it).noteDao().insertNotes(notes)
                add_note_title.setText("")
                add_note_subtitle.setText("")
                add_note_desc.setText("")
                layoutImage.visibility = View.GONE
                img_view.visibility = View.GONE
                tvWebLink.visibility = View.GONE
                requireActivity().supportFragmentManager.popBackStack()
            }
        }
    }

    private fun addNote() {

        if (add_note_title.text.isNullOrEmpty()) {
            showToast("Note Title Required")
        }
        else if (add_note_desc.text.isNullOrEmpty()) {
            showToast("Note Description Required")
        }
        else {
            launch {
                val notes = Notes()
                notes.title = add_note_title.text.toString()
                notes.subTitle = add_note_subtitle.text.toString()
                notes.noteText = add_note_desc.text.toString()
                notes.dateTime = currentDate
                notes.color = selectedColor
                notes.imgPath = selectedImagePath
                notes.webLink = webLink

                context?.let {
                    NotesDatabase.getDatabase(it).noteDao().insertNotes(notes)
                    add_note_title.setText("")
                    add_note_subtitle.setText("")
                    add_note_desc.setText("")
                    layoutImage.visibility = View.GONE
                    img_view.visibility = View.GONE
                    tvWebLink.visibility = View.GONE
                    requireActivity().supportFragmentManager.popBackStack()
                }
            }
        }

    }

    private fun checkWebUrl() {
        if (Patterns.WEB_URL.matcher(webUrl_et.text.toString()).matches()) {
            layoutWebUrl.visibility = View.GONE
            webUrl_et.isEnabled = false
            webLink = webUrl_et.text.toString()
            tvWebLink.visibility = View.VISIBLE
            tvWebLink.text = webUrl_et.text.toString()
        } else {
            showToast("Url is not valid")
        }
    }

    private val BroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            var action = intent!!.getStringExtra("action")

            when (action!!) {

                "Blue" -> {
                    selectedColor = intent.getStringExtra("selectedColor")!!
                    color_view.setBackgroundColor(Color.parseColor(selectedColor))
                }

                "Yellow" -> {
                    selectedColor = intent.getStringExtra("selectedColor")!!
                    color_view.setBackgroundColor(Color.parseColor(selectedColor))
                }

                "Purple" -> {
                    selectedColor = intent.getStringExtra("selectedColor")!!
                    color_view.setBackgroundColor(Color.parseColor(selectedColor))
                }

                "Green" -> {
                    selectedColor = intent.getStringExtra("selectedColor")!!
                    color_view.setBackgroundColor(Color.parseColor(selectedColor))
                }

                "Orange" -> {
                    selectedColor = intent.getStringExtra("selectedColor")!!
                    color_view.setBackgroundColor(Color.parseColor(selectedColor))
                }

                "Black" -> {
                    selectedColor = intent.getStringExtra("selectedColor")!!
                    color_view.setBackgroundColor(Color.parseColor(selectedColor))
                }

                "Image" -> {
                    readStorageTask()
                    layoutWebUrl.visibility = View.GONE
                }

                "WebUrl" -> {
                    layoutWebUrl.visibility = View.VISIBLE
                }

                "DeleteNote" -> {
                    deleteNote()
                }

                else -> {
                    layoutImage.visibility = View.GONE
                    layoutWebUrl.visibility = View.GONE
                    img_view.visibility = View.GONE
                    selectedColor = intent.getStringExtra("selectedColor")!!
                    color_view.setBackgroundColor(Color.parseColor(selectedColor))
                }

            }
        }
    }


    private fun deleteNote() {
        launch {
            context?.let {
                NotesDatabase.getDatabase(it).noteDao().deleteSpecificNote(noteId)
                requireActivity().supportFragmentManager.popBackStack()
            }
        }
    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(BroadcastReceiver)
        super.onDestroy()
    }

    private fun hasReadStoragePerm(): Boolean {
        return EasyPermissions.hasPermissions(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
    }


    private fun readStorageTask() {
        if (hasReadStoragePerm()) {

            pickImageFromGallery()
        } else {
            EasyPermissions.requestPermissions(
                requireActivity(),
                getString(R.string.storage_permission),
                READ_STORAGE_PERM,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }
    }

    private fun pickImageFromGallery() {
        var intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivityForResult(intent, REQUEST_CODE_IMAGE)
        }
    }

    private fun getPathFromUri(contentUri: Uri): String? {
        var filePath: String? = null
        var cursor = requireActivity().contentResolver.query(contentUri, null, null, null, null)
        if (cursor == null) {
            filePath = contentUri.path
        } else {
            cursor.moveToFirst()
            var index = cursor.getColumnIndex("_data")
            filePath = cursor.getString(index)
            cursor.close()
        }
        return filePath
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_IMAGE && resultCode == RESULT_OK) {
            if (data != null) {
                var selectedImageUrl = data.data
                if (selectedImageUrl != null) {
                    try {
                        var inputStream = requireActivity().contentResolver.openInputStream(selectedImageUrl)
                        var bitmap = BitmapFactory.decodeStream(inputStream)
                        img_view.setImageBitmap(bitmap)
                        img_view.visibility = View.VISIBLE
                        layoutImage.visibility = View.VISIBLE

                        selectedImagePath = getPathFromUri(selectedImageUrl)!!
                    } catch (e: Exception) {
                        showToast(e.message.toString())
                    }
                }
            }
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, requireActivity())
    }


    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {

    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(requireActivity(), perms)) {
            AppSettingsDialog.Builder(requireActivity()).build().show()
        }
    }

    override fun onRationaleAccepted(requestCode: Int) {

    }

    override fun onRationaleDenied(requestCode: Int) {

    }
}