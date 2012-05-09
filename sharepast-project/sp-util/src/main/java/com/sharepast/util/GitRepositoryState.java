package com.sharepast.util;

import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: kpelykh
 * Date: 5/8/12
 * Time: 10:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class GitRepositoryState {

    String branch;                  // =${git.branch}
    String commitId;                // =${git.commit.id}
    String commitIdAbbrev;          // =${git.commit.id.abbrev}
    String buildUserName;           // =${git.build.user.name}
    String buildUserEmail;          // =${git.build.user.email}
    String buildTime;               // =${git.build.time}
    String commitUserName;          // =${git.commit.user.name}
    String commitUserEmail;         // =${git.commit.user.email}
    String commitMessageFull;       // =${git.commit.message.full}
    String commitMessageShort;      // =${git.commit.message.short}
    String commitTime;              // =${git.commit.time}

    public GitRepositoryState(Properties properties)
    {
        this.branch = properties.get("git.branch").toString();
        this.commitId = properties.get("git.commit.id").toString();
        this.commitIdAbbrev = properties.get("git.commit.id.abbrev").toString();
        this.buildUserName = properties.get("git.build.user.name").toString();
        this.buildUserEmail = properties.get("git.build.user.email").toString();
        this.buildTime = properties.get("git.build.time").toString();
        this.commitUserName = properties.get("git.commit.user.name").toString();
        this.commitUserEmail = properties.get("git.commit.user.email").toString();
        this.commitMessageShort = properties.get("git.commit.message.short").toString();
        this.commitMessageFull = properties.get("git.commit.message.full").toString();
        this.commitTime = properties.get("git.commit.time").toString();
    }

    public String getBranch() {
        return branch;
    }

    public String getCommitId() {
        return commitId;
    }

    public String getCommitIdAbbrev() {
        return commitIdAbbrev;
    }

    public String getBuildUserName() {
        return buildUserName;
    }

    public String getBuildUserEmail() {
        return buildUserEmail;
    }

    public String getBuildTime() {
        return buildTime;
    }

    public String getCommitUserName() {
        return commitUserName;
    }

    public String getCommitUserEmail() {
        return commitUserEmail;
    }

    public String getCommitMessageFull() {
        return commitMessageFull;
    }

    public String getCommitMessageShort() {
        return commitMessageShort;
    }

    public String getCommitTime() {
        return commitTime;
    }

    @Override
    public String toString() {
        return "GitRepositoryState{" +
                "branch='" + branch + '\'' +
                ", commitId='" + commitId + '\'' +
                ", commitIdAbbrev='" + commitIdAbbrev + '\'' +
                ", buildUserName='" + buildUserName + '\'' +
                ", buildUserEmail='" + buildUserEmail + '\'' +
                ", buildTime='" + buildTime + '\'' +
                ", commitUserName='" + commitUserName + '\'' +
                ", commitUserEmail='" + commitUserEmail + '\'' +
                ", commitMessageFull='" + commitMessageFull + '\'' +
                ", commitMessageShort='" + commitMessageShort + '\'' +
                ", commitTime='" + commitTime + '\'' +
                '}';
    }
}

