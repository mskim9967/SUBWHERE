import React, { useEffect, useState } from 'react';
import { Box, Image, Text, FormControl, Input, Button, Checkbox, Modal, Center, useToast, Toast } from 'native-base';
import { LineChart, BarChart, PieChart, ProgressChart, ContributionGraph } from 'react-native-chart-kit';
const moment = require('moment-timezone');

export default function StatPage() {
  const [subwayNm, setSubwayNm] = useState();
  const [trainNo, setTrainNo] = useState();
  const [carNo, setCarNo] = useState();
  const [readOnly, setReadOnly] = useState(false);
  const [nfcStatus, setNfcStatus] = useState(false);
  const [isModalOpen, setModalOpen] = useState(false);
  const [readValue, setReadValue] = useState(null);
  const toast = useToast();
  const [chartParentWidth, setChartParentWidth] = useState(0);

  const hours = () => {
    let arr = [],
      curr = moment().tz('Asia/Seoul');

    console.log(curr);

    for (let i = 0; i < 6; i++) {
      arr.push(curr.format('HH:mm'));
      curr.subtract(1, 'hours');
      console.log(arr);
    }

    return arr.reverse();
  };

  const data = {
    labels: hours(),
    datasets: [
      {
        data: [20, 45, 28, 80, 99, 43],
        color: (opacity = 1) => `rgba(40, 92, 170, ${opacity})`, // optional
        strokeWidth: 2, // optional
      },
    ],
    legend: [`접속자 수 (${moment().format('YY/MM/DD')})`], // optional
  };

  const chartConfig = {
    backgroundColor: '#ffffff',
    backgroundGradientFrom: '#ffffff',
    backgroundGradientTo: '#ffffff',
    color: (opacity = 1) => `rgba(0, 0, 0, ${opacity})`,
  };
  const style = {
    marginVertical: 8,
    borderRadius: 16,
    elevation: 2,
  };

  useEffect(() => {}, []);

  return (
    <>
      <Box style={{ width: '100%' }} onLayout={({ nativeEvent }) => setChartParentWidth(nativeEvent.layout.width)}>
        <LineChart data={data} width={chartParentWidth} height={220} chartConfig={chartConfig} style={style} />

        <ProgressChart
          data={{
            labels: ['1호선', '1호선', '1호선', '1호선', '1호선', '1호선', '1호선', '1호선'], // optional
            data: [0.4, 0.6, 0.8],
          }}
          width={chartParentWidth}
          height={220}
          strokeWidth={16}
          radius={32}
          chartConfig={chartConfig}
          style={style}
          hideLegend={false}
        />

        <BarChart
          style={style}
          data={data}
          width={chartParentWidth}
          height={220}
          yAxisLabel='$'
          chartConfig={chartConfig}
          verticalLabelRotation={30}
        />
      </Box>
    </>
  );
}
