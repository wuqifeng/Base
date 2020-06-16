package com.gas.zhihu.fragment.addmap

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment.DIRECTORY_PICTURES
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.checkSelfPermission
import com.base.lib.base.BaseFragment
import com.base.lib.di.component.AppComponent
import com.base.lib.util.ArmsUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.gas.zhihu.R
import com.gas.zhihu.app.ZhihuConstants.DEFAULT_TYPE
import com.gas.zhihu.app.ZhihuConstants.IMAGE_ZIP_FOLDER
import com.gas.zhihu.bean.MapBean
import com.gas.zhihu.dialog.TipShowDialog
import com.gas.zhihu.fragment.addmap.di.AddMapModule
import com.gas.zhihu.fragment.addmap.di.DaggerAddMapComponent
import com.gas.zhihu.fragment.addmap.mvp.AddMapContract
import com.gas.zhihu.fragment.addmap.mvp.AddMapPresenter
import com.gas.zhihu.utils.MapBeanDbUtils
import com.lib.commonsdk.utils.FileUtils
import com.lib.commonsdk.utils.TimeUtils
import com.lib.commonsdk.utils.Utils
import kotlinx.android.synthetic.main.zhihu_fragment_add_map.*
import java.io.File


/**
 * ================================================
 * desc:添加地图集合
 *
 * created by author ljx
 * date  2020/5/21
 * email 569932357@qq.com
 *
 * ================================================
 */
class AddMapFragment : BaseFragment<AddMapPresenter>(), AddMapContract.View {
    companion object {
        fun newInstance(): AddMapFragment {
            val fragment = AddMapFragment()
            return fragment
        }

        const val REQUEST_PICK_IMAGE = 101
        const val REQUEST_CROP_PHOTO = 102
        const val REQUEST_STORAGE_PERMISSION = 103
    }


    private var imageFile: File? = null // 声明File对象
    private var imageUri: Uri? = null // 裁剪后的图片uri

    override fun setupFragmentComponent(appComponent: AppComponent) {
        DaggerAddMapComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .addMapModule(AddMapModule(this))
                .build()
                .inject(this)
    }

    override fun initView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.zhihu_fragment_add_map, container, false);
    }

    override fun initData(savedInstanceState: Bundle?) {
        titleView.setOnBackListener {
           killMyself()
        }
        imgAddress.setOnClickListener {
            requestStoragePermission()
        }
        btnCommit.setOnClickListener {
            commitMapInfo()
        }

    }


    override fun setData(data: Any?) {

    }

    override fun showLoading() {

    }

    override fun hideLoading() {

    }

    override fun showMessage(message: String) {
        ArmsUtils.snackbarText(message)
    }

    override fun launchActivity(intent: Intent) {
        ArmsUtils.startActivity(intent)
    }

    override fun killMyself() {
       activity?.finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (resultCode == RESULT_OK) {
            when (requestCode) {
                REQUEST_PICK_IMAGE -> {
                    data?.data?.apply {
                        val intent = Intent("com.android.camera.action.CROP")
                        intent.setDataAndType(this, "image/*");
                        // 设置裁剪
                        intent.putExtra("crop", "true");
                        intent.putExtra("scale", true);
                        // aspectX aspectY 是宽高的比例
                        intent.putExtra("aspectX", 1);
                        intent.putExtra("aspectY", 1);
                        // outputX outputY 是裁剪图片宽高
                        intent.putExtra("outputX", 400);
                        intent.putExtra("outputY", 400);
                        intent.putExtra("return-data", false);
                        // 创建文件保存裁剪的图片
                        createImageFile()
                        imageUri = Uri.fromFile(imageFile)
                        if (imageUri != null) {
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString())
                            activity?.startActivityForResult(intent, REQUEST_CROP_PHOTO)
                        }
                    }
                }
                REQUEST_CROP_PHOTO -> {
                    imageFile?.let {
                        displayImage(it.path)
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUEST_STORAGE_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 用户同意，执行相应操作
                selectImage()
            } else {
                // 用户不同意，向用户展示该权限作用
                showMessage("请开启权限，否则图片选择功能不可用")
            }
        }
    }

    private fun commitMapInfo() {
        if (etKeyName.text.isBlank()) {
            showMessage("键值不能为空")
            return
        }
        if(etKeyName.text.toString()== DEFAULT_TYPE){
            showMessage("键值格式错误")
            return
        }
        if (etMapName.text.isBlank()) {
            showMessage("电站名称不能为空")
            return
        }
        if (etAddressInfo.text.isBlank()) {
            showMessage("地址信息不能为空")
            return
        }
        if (etAddressLat.text.isBlank() || tvFileTitle.text.isBlank()) {
            showMessage("经纬度不能为空")
            return
        }
        if (imageFile == null && imageUri == null) {
            showMessage("请传入地址信息截图")
            return
        }
        val bean = MapBean()
        bean.keyName=etKeyName.text.toString()
        bean.mapName=etMapName.text.toString()
        bean.locationInfo=etAddressInfo.text.toString()
        bean.latitude=etAddressLat.text.toString().toDouble()
        bean.longitude=etAddressLon.text.toString().toDouble()
        bean.pathName="${etKeyName.text}.jpg"
        if( MapBeanDbUtils.insertMapBean(bean)){
            val file = File("${Utils.getExternalFilesDir(activity!!)}${File.separator}${IMAGE_ZIP_FOLDER}",
                    "${etKeyName.text}.jpg")
            if(FileUtils.copy(imageFile,file)){
                TipShowDialog.show(activity!!, "提示", "保存成功") { killMyself() }
            }else{
                showMessage("文件添加失败请重试")
                MapBeanDbUtils.delete(bean.keyName)
            }

        }else{
            showMessage("已经添加过该厂站信息...")
        }

    }

    /**
     * 创建File保存图片
     */
    private fun createImageFile() {

        try {
            val folder=Utils.getExternalFilesDir(activity!!, DIRECTORY_PICTURES)
            if(folder.exists()){
                FileUtils.deleteAllInDir(folder)
            }else{
                folder.mkdirs()
            }
            imageFile?.let {
                if (it.exists()) {
                    it.delete()
                }
            }
            // 新建文件
            imageFile = File(Utils.getExternalFilesDir(activity!!, DIRECTORY_PICTURES),
                    "temp_${TimeUtils.getNow()}.jpg");
        } catch (e: java.lang.Exception) {
            e.printStackTrace();
        }

    }

    private fun displayImage(imagePath: String) {
        // glide根据图片的uri加载图片
        Glide.with(this)
                .load(imagePath)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .placeholder(R.drawable.public_default_image_placeholder)// 占位图设置：加载过程中显示的图片
                .error(R.drawable.public_default_image_placeholder)// 异常占位图
                .transform(CenterCrop())
                .into(imgAddress)
    }

    private fun requestStoragePermission() {

        val hasCameraPermission = checkSelfPermission(activity!!, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (hasCameraPermission == PackageManager.PERMISSION_GRANTED) {
            selectImage()
        } else {
            // 没有权限，向用户申请该权限
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_STORAGE_PERMISSION);
            }
        }

    }

    private fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        activity?.startActivityForResult(intent, REQUEST_PICK_IMAGE)
    }


}
