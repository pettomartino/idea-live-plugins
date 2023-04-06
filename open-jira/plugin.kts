import liveplugin.openInBrowser
import liveplugin.registerAction

val jiraURL = "<HERE YOUR JIRA URL>"

registerAction(id = "Open in Jira", keyStroke = "ctrl alt shift J") { event: com.intellij.openapi.actionSystem.AnActionEvent ->
    liveplugin.runShellScript("cd " + event.project?.basePath + " && git branch --show-current").also { commandResult ->
        if (commandResult.stderr.isNotBlank()) {
            return@registerAction
        }
        
        val currentBranchName = commandResult.stdout
        val jiraId = Regex("^(\\w+-\\d+)-").find(currentBranchName)?.groups?.elementAt(1)?.value
        if (!jiraId.isNullOrBlank()) {
            openInBrowser("$jiraURL/browse/$jiraId")
        }
    }
}

