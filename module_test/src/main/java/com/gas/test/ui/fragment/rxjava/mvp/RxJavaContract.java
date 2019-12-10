package com.gas.test.ui.fragment.rxjava.mvp;

import com.base.lib.mvp.IModel;
import com.base.lib.mvp.IView;


/**
 * ================================================
 * Description:
 * <p>
 * Created by GasMvpFragment on 12/08/2019 15:56
 * ================================================
 */
public interface RxJavaContract {
    //对于经常使用的关于UI的方法可以定义到IView中,如显示隐藏进度条,和显示文字消息
    interface View extends IView {

    }

    //Model层定义接口,外部只需关心Model返回的数据,无需关心内部细节,即是否使用缓存
    interface Model extends IModel {

    }
}
