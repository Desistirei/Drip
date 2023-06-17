package br.com.stenox.cxc.login.protocol;

import br.com.stenox.cxc.login.check.Check;
import br.com.stenox.cxc.login.exception.InvalidCheckException;
import br.com.stenox.cxc.login.util.Util;
import br.com.stenox.cxc.Main;
import br.com.stenox.cxc.login.version.Version;
import com.comphenix.tinyprotocol.NMSReflection;
import com.comphenix.tinyprotocol.NMSReflection.FieldAccessor;
import com.comphenix.tinyprotocol.v1_8.TinyProtocol;
import com.mojang.authlib.GameProfile;
import io.netty.channel.Channel;
import org.bukkit.entity.Player;

/**
 *
 * @author netindev
 *
 */
public class LoginReceiver extends TinyProtocol {

	private Version version = Version.getPackageVersion();

	private FieldAccessor<GameProfile> profileField = NMSReflection.getField("{nms}.PacketLoginInStart",
			GameProfile.class, 0);

	@Override
	public Object onPacketInAsync(Player receiver, Channel channel, Object packet) {
		if (this.profileField.hasField(packet)) {
			if (this.receiveLogin(packet, channel)) {
				return null;
			}
		}
		return super.onPacketInAsync(receiver, channel, packet);
	}

	private boolean receiveLogin(Object packet, Channel channel) {
		boolean check;
		try {
			check = Check.fastCheck(this.profileField.get(packet).getName());
		} catch (InvalidCheckException e) {
			e.printStackTrace();
			return false;
		}	
		Main.getInstance().getStorage().setPremium(this.profileField.get(packet).getName(), check);
		if (check) {
			return false;
		}
		try {
			Class<?> loginClass = Class.forName("br.com.stenox.cxc.login.version.rewrite." + this.version.toString());
			loginClass.getConstructors()[0].newInstance(Util.networkList(channel.remoteAddress(),
					this.version.getServerConnection(), this.version.getNetworkManager()),
					this.profileField.get(packet).getName());
		} catch (Exception e) {
			e.printStackTrace();
			return true;
		}
		return true;
	}

}