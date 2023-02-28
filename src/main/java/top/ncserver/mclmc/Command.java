package top.ncserver.mclmc;
/*
@author：MakesYT
@program：Ncharge
*/

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.io.IOException;
import java.util.logging.Logger;

public class Command implements CommandExecutor {
	public Logger logger= MclMC.getPlugin(MclMC.class).getLogger();
	@Override
	public boolean onCommand(CommandSender commandSender, org.bukkit.command.Command command, String s, String[] strings) {
		if (strings.length==0){
			commandSender.sendMessage("指令不完整");
		}else if (strings[0].equals("shutdown")){
			logger.info("强制销毁进程");
			MclMC.INSTANCE.process.destroy();
		}else if (strings[0].equals("start")){
			logger.info("启动进程");
			MclMC.INSTANCE.runMcl();
		} else {
			StringBuilder sb = new StringBuilder();
			for (String string : strings) {
				sb.append(string);
				sb.append(" ");
			}
			try {
				MclMC.INSTANCE.bufferedWriter.write(sb.toString());
				MclMC.INSTANCE.bufferedWriter.newLine();
				MclMC.INSTANCE.bufferedWriter.flush();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}

		}
		return true;
	}
}
