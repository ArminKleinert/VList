import de.kleinert.vlist.VList;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        var vl = VList.listToVList(List.of(1,2,3));
        System.out.println(vl);
    }
}
