package net.awyvrix.bot.data.config;

public final class Config {
    public boolean TEST_MODE = false;
    public boolean FUN_MODE = true;

    public int BACKUP_PERIOD = 3;

    public int TEST_NETWORK = 15;
    public int TEST_PUBLICATION_PERIOD = 5;
    public int TEST_USER_PERIOD = 600;
    public int MAX_NETWORK_REPORTS = 5;

    public boolean ALLOW_PHOTO = true;
    public boolean ALLOW_VIDEO = true;
    public boolean ALLOW_DOCUMENT = false;

    public int MAX_PHOTO_SIZE = 20;
    public int MAX_VIDEO_SIZE = 60;
    public int MAX_DOCUMENT_SIZE = 20;

    public boolean KEEP_SUBMISSIONS = true;
    public int PUBLICATION_PERIOD = 1800;
    public int USER_PERIOD = 600;

    public int MAX_ACTIONS = 24;
    public int MAX_RECOVERY = 6;

    public int BASIC_ACTIONS = 6;
    public int BASIC_RECOVERY = 1;

    public int ACTIONS_DELTA = 10;

    public float SPAM_FINE = 0.01F;
    public float REPEAT_FINE = 0.01F;
    public float MOD_SPAM_FINE = 0.03F;
    public float ACCEPT_BONUS = 0.08F;
}