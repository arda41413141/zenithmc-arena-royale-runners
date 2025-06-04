
import React from 'react';
import { Team, LootItem } from '../hooks/useCircleGame';

interface CircleArenaProps {
  teams: Team[];
  activeTeams: Team[];
  lootItems: LootItem[];
  currentPhase: string;
}

const CircleArena: React.FC<CircleArenaProps> = ({ teams, activeTeams, lootItems, currentPhase }) => {
  const getLootColor = (rarity: string) => {
    switch (rarity) {
      case 'legendary': return '#FFD700';
      case 'epic': return '#9932CC';
      case 'rare': return '#1E90FF';
      default: return '#808080';
    }
  };

  const getLootIcon = (type: string) => {
    switch (type) {
      case 'weapon': return '⚔️';
      case 'armor': return '🛡️';
      case 'potion': return '🧪';
      case 'food': return '🍎';
      default: return '📦';
    }
  };

  return (
    <div className="relative w-96 h-96 mx-auto">
      {/* Arena Circle */}
      <div className="absolute inset-0 rounded-full border-4 border-green-600 bg-gradient-to-br from-green-100 to-green-200 shadow-2xl">
        <div className="absolute inset-4 rounded-full border-2 border-green-400 bg-gradient-to-br from-green-50 to-green-100">
          
          {/* Center Loot Area */}
          <div className="absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2">
            <div className="w-20 h-20 rounded-full bg-gradient-to-br from-yellow-400 to-yellow-600 shadow-lg flex items-center justify-center">
              <span className="text-2xl">💎</span>
            </div>
          </div>

          {/* Loot Items */}
          {currentPhase === 'looting' && lootItems.map((loot) => (
            <div
              key={loot.id}
              className="absolute animate-bounce"
              style={{
                left: `calc(50% + ${loot.position.x}px)`,
                top: `calc(50% + ${loot.position.y}px)`,
                transform: 'translate(-50%, -50%)'
              }}
            >
              <div 
                className="w-8 h-8 rounded-lg flex items-center justify-center shadow-lg text-sm animate-pulse"
                style={{ backgroundColor: getLootColor(loot.rarity) }}
                title={loot.name}
              >
                {getLootIcon(loot.type)}
              </div>
            </div>
          ))}

          {/* Player Positions */}
          {activeTeams.map((team, teamIndex) => (
            team.players.map((player, playerIndex) => {
              const angle = (Math.PI * 2 * teamIndex) / activeTeams.length + (playerIndex * 0.3);
              const radius = 140;
              const x = Math.cos(angle) * radius;
              const y = Math.sin(angle) * radius;

              return (
                <div
                  key={player.id}
                  className="absolute animate-pulse"
                  style={{
                    left: `calc(50% + ${x}px)`,
                    top: `calc(50% + ${y}px)`,
                    transform: 'translate(-50%, -50%)'
                  }}
                >
                  <div className="flex flex-col items-center">
                    <div 
                      className="w-10 h-10 rounded-full border-2 shadow-lg overflow-hidden"
                      style={{ borderColor: team.color }}
                    >
                      <img 
                        src={player.avatar} 
                        alt={player.name}
                        className="w-full h-full object-cover"
                        onError={(e) => {
                          e.currentTarget.src = `https://ui-avatars.com/api/?name=${player.name}&background=random`;
                        }}
                      />
                    </div>
                    <div className="text-xs font-bold mt-1 text-center px-1 py-0.5 bg-black bg-opacity-60 text-white rounded">
                      {player.name}
                    </div>
                    <div className="w-8 h-1 bg-gray-300 rounded-full mt-1">
                      <div 
                        className="h-full bg-green-500 rounded-full transition-all duration-300"
                        style={{ width: `${player.health}%` }}
                      />
                    </div>
                  </div>
                </div>
              );
            })
          ))}
        </div>
      </div>

      {/* Phase Indicator */}
      <div className="absolute -top-8 left-1/2 transform -translate-x-1/2">
        <div className="bg-gray-900 text-white px-4 py-2 rounded-lg font-bold">
          {currentPhase === 'looting' && '🏃 LOOT ZAMANΙ!'}
          {currentPhase === 'fighting' && '⚔️ SAVAŞ ZAMANΙ!'}
          {currentPhase === 'final' && '👑 FİNAL SAVAŞI!'}
          {currentPhase === 'waiting' && '⏳ BEKLE...'}
        </div>
      </div>
    </div>
  );
};

export default CircleArena;
