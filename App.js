import React, { useEffect, useState } from 'react';
import { NativeBaseProvider, Box, Image, Text, Center, ScrollView, Pressable, HStack, Icon } from 'native-base';
import { MaterialCommunityIcons, Ionicons } from '@expo/vector-icons';
import NfcPage from './NfcPage';
import StatPage from './StatPage';

export default function App() {
  const [selected, setSelected] = React.useState(0);

  return (
    <NativeBaseProvider>
      <ScrollView>
        <Box style={{ width: '100%', padding: 30, display: 'flex', alignItems: 'center', backgroundColor: '#FAFEFF', minHeight: '100%' }} safeArea>
          <Image style={{ marginTop: 50, marginBottom: 10, resizeMode: 'contain' }} alt='logo' source={require('./img/logo.png')} size='xl' />
          <Text fontSize='md' bold color='#1A2849'>
            SubWhere 관리자 페이지
          </Text>

          {selected === 0 ? <NfcPage /> : <StatPage />}
        </Box>
      </ScrollView>

      <HStack bg='info.600' alignItems='center' safeAreaBottom>
        <Pressable cursor='pointer' opacity={selected === 0 ? 1 : 0.5} py='3' flex={1} onPress={() => setSelected(0)}>
          <Center>
            <Icon mb='1' as={<MaterialCommunityIcons name='nfc' />} color='white' size='sm' />
            <Text color='white' fontSize='12'>
              NFC
            </Text>
          </Center>
        </Pressable>
        <Pressable cursor='pointer' opacity={selected === 1 ? 1 : 0.5} py='2' flex={1} onPress={() => setSelected(1)}>
          <Center>
            <Icon mb='1' as={<Ionicons name='ios-stats-chart' />} color='white' size='sm' />
            <Text color='white' fontSize='12'>
              분석
            </Text>
          </Center>
        </Pressable>
      </HStack>
    </NativeBaseProvider>
  );
}
