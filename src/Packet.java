import java.io.Serializable;
public class Packet implements Serializable {
    private String s;
    private int packetNum;
    private int totalPackets;
    public Packet(String s, int packetNum, int totalPackets){
        this.s = s;
        this.packetNum = packetNum;
        this.totalPackets = totalPackets;
    }
    public String getChar(){
        return s;
    }
    public int getPacketNum(){
        return packetNum;
    }
    public int getTotalPackets(){
        return totalPackets;
    }
}
