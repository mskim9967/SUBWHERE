import React, { useEffect, useState } from 'react';
import { Box, Image, Text, FormControl, Input, Button, Checkbox, Modal, Center, useToast, Toast } from 'native-base';
import NFCManager, { NfcTech, NfcEvents, rNdef, Ndef } from 'react-native-nfc-manager';
import { ActivityIndicator } from 'react-native';

export default function NfcPage() {
  const [subwayNm, setSubwayNm] = useState();
  const [trainNo, setTrainNo] = useState();
  const [carNo, setCarNo] = useState();
  const [readOnly, setReadOnly] = useState(false);
  const [nfcStatus, setNfcStatus] = useState(false);
  const [isModalOpen, setModalOpen] = useState(false);
  const [readValue, setReadValue] = useState(null);
  const toast = useToast();

  const initNFC = async () => {
    const isNFCSupport = await getIsNFCSupport();
    if (!isNFCSupport) {
      setNfcStatus(false);
      return;
    }

    const isNFCEnabled = await getIsNFCEnabled();
    if (!isNFCEnabled) {
      setNfcStatus(false);
      return;
    }
    setNfcStatus(true);
  };
  const getIsNFCSupport = async () => {
    try {
      await NFCManager.start();
      await NFCManager.isSupported();
      return true;
    } catch (e) {
      return false;
    }
  };
  const getIsNFCEnabled = async () => {
    try {
      const isNFCEnabled = await NFCManager.isEnabled();
      return isNFCEnabled;
    } catch (e) {
      return false;
    }
  };
  const writeNdef = async (data) => {
    let result = false;

    try {
      await NFCManager.requestTechnology(NfcTech.Ndef);

      const bytes = Ndef.encodeMessage([
        Ndef.uriRecord(
          `https://ottitor.shop/subway?subwayNm=${encodeURI(data.subwayNm) || ''}&trainNo=${data.trainNo || ''}&carNo=${data.carNo || ''}`
        ),
      ]);

      if (bytes) {
        await NFCManager.ndefHandler.writeNdefMessage(bytes);
        if (data.readOnly) await NFCManager.ndefHandler.makeReadOnly();
        Toast.show({ description: 'NFC 작성 성공' });
        result = true;
      }
    } catch (ex) {
      Toast.show({ description: 'NFC 작성 실패' });
      // console.warn(ex);
    } finally {
      NFCManager.cancelTechnologyRequest();
      setModalOpen(false);
    }

    return result;
  };

  const readTag = async () => {
    await NFCManager.registerTagEvent();
  };

  useEffect(() => {
    NFCManager.setEventListener(NfcEvents.DiscoverTag, (tag) => {
      try {
        let url = Ndef.decodeMessage(tag.ndefMessage && tag.ndefMessage[0].payload)[0].type;
        let regex = /[?&]([^=#]+)=([^&#]*)/g,
          params = {},
          match;
        while ((match = regex.exec(url))) {
          params[match[1]] = match[2];
        }
        setReadValue(params);
        toast.show({ description: 'NFC 읽기 성공' });
      } catch (e) {
        toast.show({ description: 'NFC 읽기 실패' });
      }
    });

    return () => {
      NFCManager.setEventListener(NfcEvents.DiscoverTag, null);
    };
  }, []);

  useEffect(() => {
    const a = async () => {
      await initNFC();
      await readTag();
    };
    a();
  }, []);

  return (
    <>
      <Box style={{ width: '100%' }}>
        <Box
          style={{
            marginTop: 40,
            width: '100%',
            display: 'flex',
            alignItems: 'center',
            backgroundColor: '#ffffff',
            borderRadius: 20,
            overflow: 'hidden',
            paddingHorizontal: 40,
            paddingVertical: 20,
            elevation: 2,
          }}
        >
          <Box width='110%' style={{ flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center', marginBottom: 15 }}>
            <Text fontSize='xl' color='#333333' bold>
              NFC 쓰기
            </Text>
          </Box>
          <FormControl w='100%'>
            <FormControl.Label>호선명</FormControl.Label>
            <Input value={subwayNm} onChangeText={(e) => setSubwayNm(e)} placeholder='호선명' variant='rounded' />
          </FormControl>

          <Box style={{ width: '100%', flexDirection: 'row', justifyContent: 'space-between', marginTop: 10 }}>
            <FormControl w='49%'>
              <FormControl.Label>열차번호</FormControl.Label>
              <Input value={trainNo} onChangeText={(e) => setTrainNo(e)} placeholder='열차번호' variant='rounded' keyboardType='numeric' />
            </FormControl>

            <FormControl w='49%'>
              <FormControl.Label>객차번호</FormControl.Label>
              <Input value={carNo} onChangeText={(e) => setCarNo(e)} placeholder='객차번호' variant='rounded' keyboardType='numeric' />
            </FormControl>
          </Box>
          <Box style={{ width: '100%', flexDirection: 'row', justifyContent: 'space-between', marginTop: 25, alignItems: 'center' }}>
            <Checkbox colorScheme='info' size='md' value={readOnly} onChange={() => setReadOnly(!readOnly)}>
              <Text fontSize='sm' color='#555555'>
                읽기전용
              </Text>
            </Checkbox>
            <Button
              isDisabled={!nfcStatus || isModalOpen}
              colorScheme={nfcStatus ? 'info' : 'error'}
              borderRadius='full'
              style={{ width: 150 }}
              onPress={async () => {
                setModalOpen(true);
                if (!(await writeNdef({ subwayNm, trainNo, carNo, readOnly }))) {
                }
              }}
            >
              {isModalOpen ? <ActivityIndicator color='white' /> : nfcStatus ? 'WRITE' : 'NFC 비활성화됨'}
            </Button>
          </Box>
        </Box>

        <Box
          style={{
            marginTop: 40,
            width: '100%',
            display: 'flex',
            alignItems: 'center',
            backgroundColor: '#ffffff',
            borderRadius: 20,
            overflow: 'hidden',
            paddingHorizontal: 40,
            paddingVertical: 20,
            elevation: 2,
          }}
        >
          <Box width='110%' style={{ flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center', marginBottom: 15 }}>
            <Text fontSize='xl' color='#333333' bold>
              NFC 읽기
            </Text>
          </Box>
          {readValue ? (
            <Center>
              <Text>호선명: {decodeURI(readValue.subwayNm) || '-'}</Text>
              <Text>열차번호: {readValue.trainNo || '-'}</Text>
              <Text>객차번호: {readValue.carNo || '-'}</Text>
            </Center>
          ) : (
            <Image style={{ marginBottom: 10 }} alt='nfc tag' source={require('./img/nfc_tag.gif')} size='2xl' />
          )}
        </Box>
      </Box>
      <Modal
        isOpen={isModalOpen}
        onClose={() => {
          NFCManager.cancelTechnologyRequest();
          setModalOpen(false);
        }}
      >
        <Modal.Content>
          <Modal.CloseButton />
          <Modal.Header>NFC Ready</Modal.Header>
          <Modal.Body>
            <Center style={{ backgroundColor: '#ffffff' }}>
              <Image style={{ marginBottom: 10 }} alt='nfc tag' source={require('./img/nfc_tag.gif')} size='2xl' />
              <Text>NFC 스티커를 Device 뒷면에 접촉시켜 주세요</Text>
            </Center>
          </Modal.Body>
        </Modal.Content>
      </Modal>
    </>
  );
}
