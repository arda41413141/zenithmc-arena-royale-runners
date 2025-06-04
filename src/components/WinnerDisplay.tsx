
import React from 'react';
import { Team } from '../hooks/useCircleGame';

interface WinnerDisplayProps {
  winnerTeam: Team | null;
  isVisible: boolean;
}

const WinnerDisplay: React.FC<WinnerDisplayProps> = ({ winnerTeam, isVisible }) => {
  if (!isVisible || !winnerTeam) return null;

  return (
    <div className="fixed inset-0 bg-black bg-opacity-80 flex items-center justify-center z-50 animate-fade-in">
      <div className="bg-gradient-to-br from-yellow-400 to-yellow-600 p-8 rounded-2xl shadow-2xl text-center max-w-md animate-scale-in">
        <div className="text-6xl mb-4">👑</div>
        <h1 className="text-4xl font-bold text-gray-900 mb-6">KAZANAN!</h1>
        
        <div 
          className="bg-white rounded-lg p-6 mb-6 border-4"
          style={{ borderColor: winnerTeam.color }}
        >
          <h2 
            className="text-2xl font-bold mb-4"
            style={{ color: winnerTeam.color }}
          >
            {winnerTeam.name}
          </h2>
          
          <div className="flex justify-center space-x-4">
            {winnerTeam.players.map((player) => (
              <div key={player.id} className="text-center">
                <div className="relative">
                  <img 
                    src={player.avatar}
                    alt={player.name}
                    className="w-16 h-16 rounded-full border-4 border-yellow-400 shadow-lg animate-bounce"
                    onError={(e) => {
                      e.currentTarget.src = `https://ui-avatars.com/api/?name=${player.name}&background=random`;
                    }}
                  />
                  <div className="absolute -top-2 -right-2 text-2xl">🥇</div>
                </div>
                <div className="font-bold text-gray-800 mt-2">{player.name}</div>
                <div className="text-sm text-gray-600">Champion</div>
              </div>
            ))}
          </div>
        </div>
        
        <div className="text-gray-800 font-medium">
          🎉 Tebrikler! Bu takım tüm rakiplerini yenerek şampiyon oldu! 🎉
        </div>
      </div>
    </div>
  );
};

export default WinnerDisplay;
