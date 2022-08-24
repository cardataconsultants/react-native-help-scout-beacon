import * as React from 'react';

import { StyleSheet, View, Button } from 'react-native';
import Beacon from 'react-native-help-scout-beacon';

export default function App() {
  return (
    <View style={styles.container}>
      <Button
        title="Open HS"
        onPress={() => {
          console.log('Opening Beacon');
          Beacon.open({
            beaconId: '8def2df8-5359-491f-8eee-66df8f0348ef',
            color: '#497E76', // mint turquoise
          });
        }}
      />
      <Button
        title="Login"
        onPress={() => {
          Beacon.identify({
            email: 'gabriel@betaacid.co',
          });
        }}
      />
      <Button
        title="Logout"
        onPress={() => {
          Beacon.logout();
        }}
      />
      <Button
        title="Suggest Google"
        onPress={() => {
          Beacon.suggest([
            {
              type: 'link',
              link: 'https://www.google.com/',
              label: 'Custom RN Suggestion',
            },
          ]);
        }}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
});
