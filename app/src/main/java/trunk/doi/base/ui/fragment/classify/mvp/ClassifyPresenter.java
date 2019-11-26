package trunk.doi.base.ui.fragment.classify.mvp;

import android.app.Application;

import com.base.lib.di.scope.FragmentScope;
import com.base.lib.mvp.BasePresenter;
import com.base.lib.mvp.IModel;

import javax.inject.Inject;

@FragmentScope
public class ClassifyPresenter extends BasePresenter<IModel, ClassifyContract.View> {


    @Inject
    Application mApplication;

    @Inject
    public ClassifyPresenter(ClassifyContract.View rootView) {
        super(rootView);
    }

    @Override
    public void onDestroy() {
        mApplication = null;
        super.onDestroy();
    }

}
