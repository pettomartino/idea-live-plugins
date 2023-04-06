import com.intellij.openapi.actionSystem.AnActionEvent
import liveplugin.*

import git4idea.ui.branch.*
import git4idea.branch.*
// depends-on-plugin Git4Idea

registerAction(id = "Show Git Recent Branches", keyStroke = "ctrl alt shift B") { event: AnActionEvent ->
    val project = event.project!!
    val repositories = git4idea.repo.GitRepositoryManager.getInstance(project).repositories

    runShellScript("cd ${event.project?.basePath} && git branch --sort=-committerdate").also { commandResult ->
        if (commandResult.stderr.isNotBlank()) {
            show("List branch error: ${commandResult.stderr}")
            return@registerAction
        }

        val branches = commandResult.stdout.split("\n")
        val actionGroup = PopupActionGroup("Recent Branches")

        for (index in 0..branches.size.coerceAtMost(15)) {
            val branchName = branches[index]
            actionGroup.addAction(AnAction(branchName) {
                GitBranchPopupActions
                    .LocalBranchActions
                    .CheckoutAction
                    .checkoutBranch(project, repositories, branchName.trim())
            })
        }
        actionGroup
            .createPopup(event.dataContext)
            .showCenteredInCurrentWindow(event.project!!)
    }
}

if (!isIdeStartup) show("Git Recent Branches Loaded")
