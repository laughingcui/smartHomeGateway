package org.pjsip.help;

import org.pjsip.pjsua2.LogEntry;
import org.pjsip.pjsua2.LogWriter;

/**
 * Created by will on 2016/1/24.
 */
public class MyLogWriter extends LogWriter
{
    @Override
    public void write(LogEntry entry)
    {
        System.out.println(entry.getMsg());
    }
}