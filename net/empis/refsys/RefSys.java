/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package net.empis.refsys;

import code.husky.mysql.MySQL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Halička
 */
public class RefSys extends JavaPlugin{
    private static final Logger log = Logger.getLogger("Minecraft");
    MySQL MySQL = new MySQL(RefSys.this, "HOST", "PORT(DEFAULT 3306)", "DATABAZA", "USER", "PASS");
    Connection c = null;
    @Override
    public void onEnable(){
        log.info("RefSys plugin enabled");
        c = MySQL.openConnection();
    }
    @Override
    public void onDisable(){
        log.info("RefSys plugin disabled");
    }
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] arg){
        if(cmd.getName().equalsIgnoreCase("ref")){
            if(arg.length > 1){
                sender.sendMessage("[RefSys] Moc vela argumentov.");
                return false;
            }
            if(arg.length < 1){
                sender.sendMessage("[RefSys] Moc malo argumentov.");
                return false;
            }
            String hrac = arg[0];
            try {
            Statement statement = c.createStatement();
            ResultSet res;
                res = statement.executeQuery("SELECT * FROM `authme` WHERE `username` = '" + arg[0] + "';");               
                if (res.next()) {
                    //Overenie existujuceho uzivatela
                    if(res.getString("username").equals(arg[0])){
                        //Uzivatel existuje, overenie ci uz ma nastaveneho referala
                        ResultSet res2;
                        res2 = statement.executeQuery("SELECT `referal` FROM `authme` WHERE `username` = '" + sender.getName() + "';");
                        if(res2.next()){
                            sender.sendMessage("[RefSys] Už si zadal tento príkaz!");
                            return false;
                        }
                        //Pridanie kreditov a poctu referalov
                         statement.executeUpdate("UPDATE `authme` SET `kredity`=`kredity`+'10', `nalakany`=`nalakany`+'1' WHERE `username` = '" + arg[0] + "';");
                         //Pridanie referala
                         statement.executeUpdate("UPDATE `authme` SET `referal`='" + arg[0] + "' WHERE `username` = '" + sender.getName() + "';");
                         sender.sendMessage("[RefSys] Úspešné!");
                        return false;
                    }
                }
                sender.sendMessage("[RefSys] Hráč neexistuje!");
            } catch (SQLException ex) {
                Logger.getLogger(RefSys.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return false;
    }
}
