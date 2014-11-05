package hk.ust.stop.idao;

import java.util.ArrayList;

import hk.ust.stop.model.GoodsInformation;

public interface BaseDaoInterface {
	public void insert(long userId, GoodsInformation info, int flag);
	public ArrayList<GoodsInformation> queryAllRecord();
}
