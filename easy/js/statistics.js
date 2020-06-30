$(function(){
    toSearch();
    //年份选择框
    laydate.render({
        elem: '#searchTime', //指定元素 
        type: 'year'
    });
})
function randomValue() {
    return Math.round(Math.random()*1000);
}

function toSearch(){
    $.ajax({
        url:requestIP + "project/getStatisticsData",
        type:'get',
        dataType : "json",
        contentType : 'application/json;charset=utf-8',
        data:{
            projectDate: $("#searchTime").val()},
        success : function(data) {
            
            console.log(data);
            console.log(data.status);
            if(data.status == 1){
                var dataList = data.list;
                var myChart = echarts.init(document.getElementById('pie'));
                var myChart2 = echarts.init(document.getElementById('map'));
                
                // 地图
                var mapOption = {
                    title: {
                        text: '各单位投资情况',
                        subtext: '',
                        x: 'left'
                    },
                    tooltip: {
                            formatter:function(params,ticket, callback){
                                return params.seriesName+'<br />'+params.name+'：'+params.value
                            }
                        },
                    visualMap: {
                        min: 0,
                        max: 300,
                        left: 'left',
                        top: 'bottom',
                        text: ['高','低'],
                        inRange: {
                            color: ['#e0ffff', '#006edd']
                        },
                        show:true
                    },
                    geo: {
                        map: 'china',
                        roam: false,
                        zoom:1.23,
                        label: {
                            normal: {
                                show: true,
                                fontSize:'10',
                                color: 'rgba(0,0,0,0.7)'
                            }
                        },
                        itemStyle: {
                            normal:{
                                borderColor: 'rgba(0, 0, 0, 0.2)'
                            },
                            emphasis:{
                                areaColor: '#F3B329',
                                shadowOffsetX: 0,
                                shadowOffsetY: 0,
                                shadowBlur: 20,
                                borderWidth: 0,
                                shadowColor: 'rgba(0, 0, 0, 0.5)'
                            }
                        }
                    },
                    series : [
                        {
                            name: '信息量',
                            type: 'map',
                            geoIndex: 0,
                            data:dataList
                        }
                    ]
                };
                var option = {
                    title: {
                        text: '各单位投资情况',
                        subtext: '',
                        x: 'left'
                    },
                    tooltip: {
                        trigger: 'item',
                        formatter: "{a} <br/>{b}({d}%)"
                    },
                    legend: {
                        orient: 'vertical',
                        left: '30%',  //图例距离左的距离
                        y: 'bottom',  //图例上下居中
                        data: [{
                            name:"吉林",
                            icon:"circle"},
                            {
                                name:"辽宁",
                                icon:"circle"
                            },
                            {
                                name:"河北",
                                icon:"circle"
                            },
                            {
                                name:"山西",
                                icon:"circle"
                            },
                            {
                                name:"山东",
                                icon:"circle"
                            }
                        ],
                        formatter:function(name){
                            let target;
                            for(let i=0;i<dataList.length;i++){
                                if(dataList[i].name===name){
                                    target=dataList[i].value
                                }
                            }
                            let arr = [name+"                         "+target]
                            return arr.join("\n")
                        },
                        textStyle:{
                            fontSize:14
                        }
                    },
                    color: ['rgb(203,155,255)', 'rgb(149,162,255)', 'rgb(58,186,255)',
                            'rgb(119,168,249)', 'rgb(235,161,159)'],//五个数据，五个颜色
                    series: [
                    {
                        name: '',
                        type: 'pie',
                        radius: ['50%','65%'],  //图的大小
                        center: ['50%', '40%'], //图的位置，距离左跟上的位置
                        emphasis: {
                            label: {
                                show: true,
                                fontSize: '30',
                                fontWeight: 'bold'
                            }
                        },
                        labelLine: {
                            show: false
                        },
                        data:dataList
                    }
                    ]
                };
                myChart.setOption(option);
                myChart2.setOption(mapOption);
            }else{
                $.messager.alert('温馨提示',"查询失败！")
            }
        }
    })
}