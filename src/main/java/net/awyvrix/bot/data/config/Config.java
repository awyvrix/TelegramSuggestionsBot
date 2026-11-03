package net.awyvrix.bot.data.config;

public final class Config {
    public boolean TEST_MODE = false;
    public boolean FUN_MODE = true;

    public int BACKUP_PERIOD = 1;

    public int TEST_NETWORK = 15;
    public int TEST_PUBLICATION_PERIOD = 5;
    public int TEST_USER_PERIOD = 600;

    public int PUBLICATION_PERIOD = 1800;
    public int USER_PERIOD = 600;

    public int MAX_ACTIONS = 24;
    public int MAX_RECOVERY = 6;
    public int BASIC_ACTIONS = 6;
    public int ACTIONS_DELTA = 10;

    public float SPAM_FINE = 0.01F;
    public float REPEAT_FINE = 0.01F;
    public float MOD_SPAM_FINE = 0.03F;
    public float ACCEPT_BONUS = 0.08F;
}