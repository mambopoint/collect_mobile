package org.odk.collect.android.activities

import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import org.odk.collect.analytics.Analytics
import org.odk.collect.android.R
import org.odk.collect.android.analytics.AnalyticsEvents
import org.odk.collect.android.databinding.FirstLaunchLayoutBinding
import org.odk.collect.android.injection.DaggerUtils
import org.odk.collect.android.projects.CurrentProjectProvider
import org.odk.collect.android.projects.ManualProjectCreatorDialog
import org.odk.collect.android.projects.QrCodeProjectCreatorDialog
import org.odk.collect.android.version.VersionInformation
import org.odk.collect.androidshared.ui.DialogFragmentUtils
import org.odk.collect.androidshared.ui.GroupClickListener.addOnClickListener
import org.odk.collect.projects.Project
import org.odk.collect.projects.ProjectsRepository
import javax.inject.Inject

class FirstLaunchActivity : CollectAbstractActivity() {

    @Inject
    lateinit var projectsRepository: ProjectsRepository

    @Inject
    lateinit var versionInformation: VersionInformation

    @Inject
    lateinit var currentProjectProvider: CurrentProjectProvider

    public override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)
        DaggerUtils.getComponent(this).inject(this)

        if (projectsRepository.getAll().isNotEmpty()) {
            ActivityUtils.startActivityAndCloseAllOthers(this, MainMenuActivity::class.java)
            return
        }

        FirstLaunchLayoutBinding.inflate(layoutInflater).apply {
            setContentView(this.root)

            configureViaQrButton.setOnClickListener {
                DialogFragmentUtils.showIfNotShowing(
                    QrCodeProjectCreatorDialog::class.java,
                    supportFragmentManager
                )
            }

            configureManuallyButton.setOnClickListener {
                DialogFragmentUtils.showIfNotShowing(
                    ManualProjectCreatorDialog::class.java,
                    supportFragmentManager
                )
            }

            appName.text = String.format(
                "%s",
                getString(R.string.powered_by),
                // versionInformation.versionToDisplay
            )

            configureLater.addOnClickListener {
                Analytics.log(AnalyticsEvents.TRY_DEMO)

                projectsRepository.save(Project.DEMO_PROJECT)
                currentProjectProvider.setCurrentProject(Project.DEMO_PROJECT_ID)

                ActivityUtils.startActivityAndCloseAllOthers(this@FirstLaunchActivity, MainMenuActivity::class.java)
            }
        }
    }
}
