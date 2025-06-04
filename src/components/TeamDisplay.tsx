
import React from 'react';
import { Team } from '../hooks/useCircleGame';

interface TeamDisplayProps {
  teams: Team[];
  currentRound: number;
}

const TeamDisplay: React.FC<TeamDisplayProps> = ({ teams, currentRound }) => {
  const activeTeams = teams.filter(team => !team.isEliminated);
  const eliminatedTeams = teams.filter(team => team.isEliminated);

  return (
    <div className="w-full max-w-6xl mx-auto">
      <div className="text-center mb-6">
        <h2 className="text-3xl font-bold text-gray-800 mb-2">Round {currentRound}</h2>
        <p className="text-lg text-gray-600">
          Aktif Takımlar: {activeTeams.length} | Elenen Takımlar: {eliminatedTeams.length}
        </p>
      </div>

      {/* Active Teams */}
      <div className="mb-8">
        <h3 className="text-xl font-bold text-green-700 mb-4 text-center">🟢 Aktif Takımlar</h3>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          {activeTeams.map((team) => (
            <div 
              key={team.id}
              className="bg-white rounded-lg shadow-lg p-4 border-l-4 transform hover:scale-105 transition-all duration-200"
              style={{ borderLeftColor: team.color }}
            >
              <div className="flex items-center justify-between mb-3">
                <h4 className="font-bold text-lg" style={{ color: team.color }}>
                  {team.name}
                </h4>
                <div className="w-4 h-4 rounded-full" style={{ backgroundColor: team.color }} />
              </div>
              
              <div className="space-y-2">
                {team.players.map((player) => (
                  <div key={player.id} className="flex items-center space-x-3">
                    <img 
                      src={player.avatar}
                      alt={player.name}
                      className="w-8 h-8 rounded-full border-2"
                      style={{ borderColor: team.color }}
                      onError={(e) => {
                        e.currentTarget.src = `https://ui-avatars.com/api/?name=${player.name}&background=random`;
                      }}
                    />
                    <div className="flex-1">
                      <div className="text-sm font-medium">{player.name}</div>
                      <div className="w-full h-2 bg-gray-200 rounded-full">
                        <div 
                          className="h-full bg-green-500 rounded-full transition-all duration-300"
                          style={{ width: `${player.health}%` }}
                        />
                      </div>
                    </div>
                    <div className="text-xs text-gray-500">{player.health}%</div>
                  </div>
                ))}
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* Eliminated Teams */}
      {eliminatedTeams.length > 0 && (
        <div>
          <h3 className="text-xl font-bold text-red-700 mb-4 text-center">🔴 Elenen Takımlar</h3>
          <div className="grid grid-cols-1 md:grid-cols-4 gap-3">
            {eliminatedTeams.map((team) => (
              <div 
                key={team.id}
                className="bg-gray-100 rounded-lg p-3 border-l-4 border-gray-400 opacity-60"
              >
                <div className="flex items-center justify-between mb-2">
                  <h4 className="font-bold text-gray-600">{team.name}</h4>
                  <div className="text-red-500">❌</div>
                </div>
                
                <div className="space-y-1">
                  {team.players.map((player) => (
                    <div key={player.id} className="flex items-center space-x-2">
                      <img 
                        src={player.avatar}
                        alt={player.name}
                        className="w-6 h-6 rounded-full grayscale"
                        onError={(e) => {
                          e.currentTarget.src = `https://ui-avatars.com/api/?name=${player.name}&background=random`;
                        }}
                      />
                      <div className="text-xs text-gray-500">{player.name}</div>
                    </div>
                  ))}
                </div>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
};

export default TeamDisplay;
