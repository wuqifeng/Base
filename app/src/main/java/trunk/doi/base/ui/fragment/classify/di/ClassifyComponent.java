/*
 * Copyright 2017 JessYan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package trunk.doi.base.ui.fragment.classify.di;

import com.base.lib.di.component.AppComponent;
import com.base.lib.di.scope.FragmentScope;

import dagger.BindsInstance;
import dagger.Component;
import trunk.doi.base.ui.fragment.classify.ClassifyFragment;
import trunk.doi.base.ui.fragment.classify.mvp.ClassifyContract;

/**
 * ================================================
 * 展示 Component 的用法
 *
 * @see <a href="https://github.com/JessYanCoding/MVPArms/wiki#2.4.6">Component wiki 官方文档</a>
 * Created by JessYan on 09/04/2016 11:17
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * ================================================
 */
@FragmentScope
@Component(dependencies = AppComponent.class)
public interface ClassifyComponent {

    void inject(ClassifyFragment fragment);

    @Component.Builder
    interface Builder {
        @BindsInstance
        ClassifyComponent.Builder view(ClassifyContract.View view);

        ClassifyComponent.Builder appComponent(AppComponent appComponent);

        ClassifyComponent build();
    }
}
