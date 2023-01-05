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
            beaconId: 'BEACON-ID',
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
            {
              type: 'article',
              articleId: 'hsarticleid',
            },
          ]);

          Beacon.open({
            beaconId: 'BEACON-ID',
            color: '#497E76', // mint turquoise
          });
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
