import Cocoa

class ThisIsWhere {

    //初始角度值
    var StartDirection: double

    //当前角度值
    var NowDirection: double

    //开始位x轴
    var StartX: double

    //开始位y轴
    var StartY: double

    //结束位x轴
    var EndX: double

    //结束位y轴
    var EndY: double

    //位移默认距离
    let DISPLACEMENT=20

//参数Start：初始角度值
//参数Now：当前角度值
//参数X：开始位x轴
//参数Y：开始位y轴

    func ==(Start: double, Now: double,X: Int32,Y: Int32) -> (isSwerve: Bool,EndX: Int32,EndY: Int32) {

//记录参数值
        StartDirection=Start;
        NowDirection=Now;
        StartX=X;
        StartY=Y;







    }

}